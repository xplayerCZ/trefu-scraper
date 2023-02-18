FROM openjdk:11-jdk
RUN mkdir /app
COPY ./build/install/trefu-scraper/ /app/
WORKDIR /app/bin
RUN ["chmod", "+x", "./trefu-scraper"]
CMD ["./trefu-scraper"]