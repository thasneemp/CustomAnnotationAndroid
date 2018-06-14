package com.example.processor;

import com.example.annotation.ActivityHelper;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;

@AutoService(Processor.class)
public class MyHelperProcessClass extends AbstractProcessor {
    private Messager mMessager;
    private Filer mFiler;
    private Map<String, String> activitiesWithPackage;
    private Elements elements;
    private static final ClassName classIntent = ClassName.get("android.content", "Intent");
    private static final ClassName classContext = ClassName.get("android.content", "Context");

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        Set<? extends Element> elementsa = roundEnvironment.getElementsAnnotatedWith(ActivityHelper.class);


        for (Element element : elementsa) {
            if (element.getKind() != ElementKind.CLASS) {
                mMessager.printMessage(Diagnostic.Kind.ERROR, "Can be applied to class.");
                return true;
            }
            TypeElement typeElement = (TypeElement) element;
            activitiesWithPackage.put(
                    typeElement.getSimpleName().toString(),
                    elements.getPackageOf(typeElement).getQualifiedName().toString());
        }

        //create a class to wrap our method
        //the class name will be the annotated class name + _Log

        TypeSpec.Builder builder = TypeSpec.classBuilder("ActivityRunner").addModifiers(Modifier.PUBLIC, Modifier.FINAL);

        for (Map.Entry<String, String> element : activitiesWithPackage.entrySet()) {
            String activityName = element.getKey();
            String packageName = element.getValue();
            ClassName activityClass = ClassName.get(packageName, activityName);
            MethodSpec intentMethod = MethodSpec
                    .methodBuilder("start" + activityName)
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                    .returns(classIntent)
                    .addParameter(classContext, "context")
                    .addStatement("return new $T($L, $L)", classIntent, "context", activityClass + ".class")
                    .build();

            MethodSpec getClass = MethodSpec
                    .methodBuilder("getClass" + activityName)
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                    .returns(Class.class)
                    .addStatement("return  $T.class", activityClass)
                    .build();

            builder.addMethod(intentMethod);
            builder.addMethod(getClass);
        }
        try {
            JavaFile.builder("com.annotationsample", builder.build())
                    .build()
                    .writeTo(mFiler);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }


    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        mMessager = processingEnvironment.getMessager();
        mFiler = processingEnvironment.getFiler();
        activitiesWithPackage = new HashMap<>();
        elements = processingEnvironment.getElementUtils();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> annotations = new HashSet<>();
        annotations.add(ActivityHelper.class.getCanonicalName());
        return annotations;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    private boolean isValidClass(TypeElement type) {
        if (type.getKind() != ElementKind.CLASS) {
            mMessager.printMessage(Diagnostic.Kind.ERROR, type.getSimpleName() + " only classes can be annotated with AwesomeLogger");
            return false;
        }

        if (type.getModifiers().contains(Modifier.PRIVATE)) {
            mMessager.printMessage(Diagnostic.Kind.ERROR, type.getSimpleName() + " only public classes can be annotated with AwesomeLogger");
            return false;
        }

        if (type.getModifiers().contains(Modifier.ABSTRACT)) {
            mMessager.printMessage(Diagnostic.Kind.ERROR, type.getSimpleName() + " only non abstract classes can be annotated with AwesomeLogger");
            return false;
        }
        return true;
    }

}
