FROM openwhisk/java8action
RUN apt-get update && apt-get install -y libfontconfig
CMD ["java", "-Dfile.encoding=UTF-8", "-Xshareclasses:cacheDir=/javaSharedCache,readonly", "-Xquickstart", "-jar", "/javaAction/build/libs/javaAction-all.jar"]