FROM amazoncorretto:17
COPY ../../../target/BookingBot-0.0.1-SNAPSHOT.jar booking_bot.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "booking_bot.jar"]