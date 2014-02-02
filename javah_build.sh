#! /bin/bash
project_loc=./
adk_platforms=${ADK}/platforms
sdk_int=android-19
classes=${project_loc}/bin/classes:${adk_platforms}/${sdk_int}/android.jar
dir=${project_loc}/jni/
target=me.wtao.service.ScreenCaptureService
javah -classpath ${classes} -d ${dir} -jni ${target}