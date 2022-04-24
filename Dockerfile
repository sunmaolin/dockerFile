# 指定参数(这个参数，运行时是不存在的)
ARG imageName=adoptopenjdk/openjdk8-openj9:alpine-slim

# java运行环境基础镜像
FROM ${imageName}

# 指定维护者信息
MAINTAINER 孙茂林 sunml727@126.com

# 指定环境变量
ENV profile=dev

# 创建目录
RUN mkdir /home/jar && mkdir /home/images

# 指定工作目录
WORKDIR /home/jar

# 声明端口
EXPOSE 7270

ARG JAR_FILE

# 将jar拷贝到镜像中
COPY ${JAR_FILE} ./app.jar

# 入口点 egd参数好像是加快启动速度的
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]

# 指定了ENTRYPOINT，CMD当作其参数传递
CMD ["--spring.profiles.active=${profile}"]