# Auction Server
Its a simple, elegant, scalable and highly available auction server for bidding on auction items.

# An Auction have following attributes:
1. Item Code -​ for which auction is running
2. Minimum Base Price -​ This is starting bidding amount, no user can place the bid
lesser than this defined price
3. Step Rate -​ minimum amount difference b/w two consecutive bids. For example, if a
user placed the bid of 1000 /- INR then the next acceptable bid will be a minimum of 1000 + Step Rate. If the step rate is 250 /- INR then the next acceptable bid should be >= 1250.
4. Status -
           
           a. RUNNING: ​Only running auctions are the candidates of placing the bid
           b. OVER: ​Once auction is over then no user can place the bid on the
              corresponding item
5. User Bids -​ All user bids should be recorded whether it was accepted or rejected.

# Tech stack used and the reason for choosing it: 
1.Springboot: 

             -Provides a good service mesh for easily setting up a server
2.Mongo: 

             -Used as the primary db for storing all the auction items with there corresponding fields.
             -Data at this store will be considered as the final source of truth in case of any disaster.
             **Why**
             -Since an auction items by definition doesnt not have a fixed schema,so its better to choose a noSql db for extending our software in future.
             -No issue of slow throughput since all activities are performed asynchronously on mongo
3.Postgres:

             -Used for storing all bidding activities done by users on auction items.
              **PS**:we can utilize mongo for the same, since we are already making use of it for primary db.
4.Redis:

             -Used for caching layer.
             -All bidding activities are recorded and maintained through redis
              **Why**
             -Redis being single threaded, takes care of concurrency and race conditions
             -Redis being in memory store, takes care of throughput/latency it supports millons of read and write speed in second
5.Kafka:

            -Used for storing all the accepted(highest) bid for a particular itemCode during each bid
            -Used for storing all the bidding activity, as it happens in time for later processing
            -Serves as the source point for broadcasting the accepted(highest) bidding events to all the users
             **Why**
            -Being a robust message broker, we utilize it for our streaming source.
            -Provides scalability with the help of partitions and multiple consumer group support
6.AspectJ:

            -Since we have the requirement of capturing all bidding activity, its better to handle this as a side concern by utilizing aspects.Since we can tolerate              missing some activity records, but we can't tolerate missing actual bidding process.
7.Junit:
            -Used for unit testing
            
8.Swagger:

            -Integrated with the system for easy api documentation and access
            
# How to run the app
**First thing first: lets get the dependencies out of the way->**

1.Download and run postgres -> [https://www.tutorialspoint.com/postgresql/postgresql_environment.htm]

2.Download and run mongo    -> [https://docs.mongodb.com/manual/tutorial/install-mongodb-on-os-x/]
                               [https://docs.mongodb.com/manual/tutorial/install-mongodb-on-windows/]

3.Download and run Redis    -> [https://redis.io/topics/quickstart]

4.Download and run kafka    -> [https://dzone.com/articles/kafka-setup]

**Now our app**

1.checkout code locally

2.mvn clean install -> generates a jar file

3.If using IDE-> mvn spring-boot:run
       
          else-> java -jar generatedJarFileName
  **HINT: follow general rule of running any spring boot app**
 
# Once server is up:
checkout the swagger api:- http://localhost:8080/auction/swagger-ui.html/
#For accessing the apis you need to be authenticated:-
**User**-     admin

**Password**- password

!Kept it simple for everyone to use it
# Before trying any api-
Hit this api for initialization of basic auction items:-  http://localhost:8080/auction/init

**Used this api for inserting some auction records for us to use the bidding process**


 
  
