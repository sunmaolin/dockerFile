# java运行环境基础镜像
FROM adoptopenjdk/openjdk8-openj9:alpine-slim

# 指定维护者信息
MAINTAINER 孙茂林 sunml727@126.com

# 创建目录
RUN mkdir /home/jar && mkdir /home/images

# 指定工作目录
WORKDIR /home/jar

# 声明端口
EXPOSE 7270

# 将jar拷贝到镜像中
COPY ./target/dockerFile.jar ./app.jar

# 入口点 egd参数好像是加快启动速度的
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]