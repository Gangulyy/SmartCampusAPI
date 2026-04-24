# Smart Campus API


## Name: Ganguli Hettiarachchi
## Student ID: 20231948/ w2120296


# Overview


A RESTful API built with Java, JAX-RS (Jersey), and Apache Tomcat for managing rooms and sensors in a university campus. It allows users to create rooms, associate sensors with rooms, and log sensor data. This API is built using REST principles, showcasing resource-oriented architecture, appropriate HTTP methods and status codes. 
The API has three primary resources: 

 • **Rooms** - correspond to physical rooms on campus. Rooms are identified by an ID, name, capacity, and have sensors installed. 
 • **Sensors** - equipment placed in rooms to measure various parameters (temperature, CO₂, occupancy, etc.) Sensors are associated with a room. 
 • **Readings** - time series data recorded by sensors. Sensors keep a record of their readings. 
 
The API is designed to mimic a basic campus monitoring system to monitor environmental data via API calls.


# Base URL


http://localhost:8080/SmartCampusAPI/api/v1


# Video Demonstration

## https://drive.google.com/file/d/1L9hq8rL1H0UCaxgIz0mjbXo21fhbxgvf/view?usp=sharing


# Technologies Used


•	Java (JDK 11 or higher) 
•	JAX-RS (Jersey implementation) 
•	Apache Tomcat 9 (servlet container) 
•	Maven (dependency management and build tool) 
•	NetBeans IDE 


# Setup and Running the Application

## Prerequisites


Ensure the following are installed and configured:
•	Java JDK 11 or higher 
•	Apache Tomcat 9 
•	NetBeans IDE with Maven support 


# Steps


**1.	Clone the repository**
git clone https://github.com/YOUR-USERNAME/SmartCampusAPI.git

**3. Open the project in NetBeans**
Go to File, then Open Project, and select the project folder

**5. Configure Tomcat Server**
Go to Tools, then Servers, then Add Server, and choose Apache Tomcat
Select the Tomcat installation directory

**7. Build the project**
Right click the project and then select Clean and Build
Wait until the console shows: BUILD SUCCESS

**9. Run the application**
Right click the project and Run

**11. Verify deployment**
Ensure the console displays:
OK - Started application at context path [/SmartCampusAPI]

**13. Access the API**
Open Postman or a browser and navigate to:
http://localhost:8080/SmartCampusAPI/api/v1 


# API Usage Examples


### 1. Discovery Endpoint

GET /api/v1
Returns general API information along with links to available resources (HATEOAS).


### 2. Get All Rooms

GET /api/v1/rooms
Retrieves a list of all rooms currently stored in the system.

### 3. Create a Room

POST /api/v1/rooms
Content-Type: application/json
{
"id": "A101",
"name": "Lecture Hall",
"capacity": 100
}
Adds a new room to the system.

### 4. Filter Sensors by Type

GET /api/v1/sensors?type=Temperature
Returns only sensors matching the specified type.

### 5. Add Sensor Reading

POST /api/v1/sensors/TEMP-001/readings
Content-Type: application/json
{
"value": 25.3
}
Adds a new reading to a sensor and updates its current value.


# Error Handling


The API returns appropriate HTTP status codes to indicate success or failure:
•	404 Not Found - resource does not exist 
•	409 Conflict - duplicate resource or invalid operation 
•	422 Unprocessable Entity - valid request format but invalid data (e.g., incorrect roomId) 
•	403 Forbidden - action not allowed (e.g., sensor under maintenance) 
•	415 Unsupported Media Type - incorrect content type 
This approach ensures clear communication between the client and server.


# Key Features

•	RESTful API using standard HTTP verbs (GET, POST, DELETE)
•	HATEOAS discovery endpoint for better discoverability 
•	DataStore in memory (Singleton pattern) 
•	Concurrency support with ConcurrentHashMap 
•	Sub-resources to manage sensor data  
•	Log request/response to a centralised log using filters 
•	Custom exception mapping to default responses
Notes
•	Data is stored in memory and will reset when the server restarts 
•	No external database is used in this implementation 
•	The API is intended for learning and demonstration purposes 


# Answers to Coursework Questions


## Part 1.1 - JAX-RS Resource Class Lifecycle

A new resource class object is created for every request. So if 100 users make requests simultaneously, 100 objects will be created. It is not a singleton, the object is created, used and then thrown away.
This creates a problem. If the resource class is used to hold data, it disappears after every request. To overcome this, a DataStore class is implemented as a Singleton, meaning only one instance exists for the lifetime of the application.. All resource classes share this one DataStore to store data. 
The other issue is that if two requests come in at the same time and both want to change the data. With a regular HashMap, this can lead to data corruption or loss. Instead, a ConcurrentHashMap is used. It is a special version of HashMap that allows several requests to safely change data at once.

## Part 1.2 - HATEOAS, Hypermedia and RESTful APIs 

HATEOAS is when API responses contain links that tell the client where to go next. Rather than the client having to know all the URLs the API tells them. 
In this project, /api/v1 endpoint has a _links section with the URLs for rooms and sensors. A client that's never seen the API before can make a single call to that endpoint and get all the URLs. 
The advantage this has over static docs is that if a URL changes, all the clients that are following the links in the responses will get the updated URL. With static documentation, all clients that used the old URL would be broken and documentation would have to be updated. HATEOAS makes the API easier to understand and consume for other programmers.

## Part 2.1 - Returning IDs or Objects in the Room Lists

If only IDs are returned in the list, the response is small and fast. But the client then needs to make an extra request for each and every ID to get the details. If there are 100 rooms, this results in 100 requests. It's inefficient and uses more resources. 
If the room objects are returned, the response is larger, but the client gets all the required information. No extra requests needed. When the campus management system's staff needs to know the details, returning full objects is more appropriate. A larger message is preferable to many smaller requests.

## Part 2.2 - Idempotency of the DELETE Operation

The DELETE operation is idempotent in this project. Idempotent means it doesn't matter how many times the operation is performed, the results remain the same. 
What this means is: sending the first DELETE request will delete the room and return 204 (success, no content). The second time sending the DELETE request, the room is already removed, so it returns 404 (not found). In any case, the room is gone. Sending it repeatedly will have the same effect. 
This is a good thing because occasionally a client will send a request and not receive a response because of a network issue. They do not know if the delete was successful or not and may retry the request. It is important that they can safely retry without making things worse.

## Part 3.1 - Consequences of Sending Wrong Content-Type with @Consumes

The POST sensor endpoint is annotated with @Consumes(MediaType.APPLICATION_JSON) that means JAX-RS can only accept JSON.
If a client tries to POST data as text/plain or application/xml, then JAX-RS will reject the request and return a 415 Unsupported Media Type response. The method itself is not executed, as JAX-RS handles the validation.
This is great because it prevents the method from being called with unsupported data formats that it doesn't recognize. The client will also receive a message explaining what is wrong and how to fix it.

## Part 3.2 - @QueryParam vs Path Segment for Filtering

In this project, filtering is implemented using GET /api/v1/sensors?type=CO2 with @QueryParam. An alternative design would be GET /api/v1/sensors/type/CO2, where type is part of the path. 
Query parameters are better for filtering because they are optional. If no type is specified all sensors are returned. With a path segment design a separate endpoint would be required to get all sensors. 
Query parameters are also easier to combine. To get all sensors that are ACTIVE and of type CO2, can be done using ?type=CO2&status=ACTIVE. With path segments, it is a complex URL like /sensors/type/CO2/status/ACTIVE, which is less readable and difficult to use. 
And if putting a filter in the path, such as /sensors/type/CO2, gives the appearance of a different resource that is stored at that URL, which it is not. Query parameters clearly show that filtering is applied to an existing collection.

## Part 4.1 - Advantages of using a Sub-Resource Locator 

SensorResource has a method that is annotated with @Path("/{sensorId}/readings"), but not with @GET or @POST. This method doesn't handle the request, it just returns a SensorReadingResource. JAX-RS will then pass the request on to it to complete the request. This is known as a sub-resource locator. 
The advantage is that the code is clean and structured. All the code related to reading data is in SensorReadingResource. SensorResource is only concerned with sensors. Each class has one job.
 If the whole thing was in one class, it would be hundreds of methods long and difficult to understand, modify and test. Separating the classes by resource level, keeps the classes small and isolated. New features can be added for readings without changing the sensor class.

## Part 4.2 - Storing Historical Data and Side Effects 

SensorReadingResource is responsible for returning all previous readings for a sensor (GET), and for posting a new reading (POST). Readings are maintained in a HashMap with the sensor ID as the key to the list of readings for that sensor. When a new reading is posted, an important side effect occurs sensor.setCurrentValue(reading.getValue()) is called. This ensures the parent sensor object's currentValue is updated to reflect the latest reading.
Otherwise, if someone gets the sensor details, they'll see an outdated currentValue, even though there are new readings. The data would be inconsistent. By posting it on each reading, the API ensures that all endpoints consistently reflect the latest state.

## Part 5.2 - Why 422 is More Semantically Accurate than 404

When a user creates a sensor with an invalid roomId, a 422 response is returned instead of 404.
404 means that the requested URL doesn't exist on the server. However, in this case, the URL used (POST /api/v1/sensors) is valid. It has nothing to do with the URL, but rather with the content of the JSON request, specifically the roomId value refers to a room that doesn't exist. 
The response code 422 means that the request was sent to the right endpoint and the JSON was valid, but something about the content of the JSON is not sensible. This is a more specific error. It tells the client developer that the problem lies in the request content rather than the endpoint itself. This helps them fix the issue in a timely fashion. 

## Part 5.4 - Security Risks of Exposing Java Stack Traces

When a Java stack trace is exposed, it reveals significant internal information. It reveals the names of internal classes and packages, providing insight into the structure of the application. It may also reveal third party libraries and their specific versions, which can be checked against known vulnerabilities in public databases.
It may also reveal paths to files on the server and line numbers in the code, so the attacker can see how the application is structured and potentially reveal weak points. The GlobalExceptionMapper fixes this problem by intercepting all possible errors in the application and only returning a generic error like "something went wrong" to the client. The actual error message will only be logged into the server's error log. This way the API never leaks anything useful to someone trying to attack it. 

## Part 5.5 - Why Filters are Better than Manual Logging

 A filter is a single class that automatically gets called for every request and response of the API. It only needs to be implemented once. If logging were added manually inside every resource method, every new endpoint added in the future would need someone to remember to add the logging line. Forgetting just once means that the endpoint has no logs at all. Changing the log format later would mean editing every single method in the whole project.
With a filter, if logging requirements change, updating a single place applies the change to all API calls. The ContainerRequestFilter part is executed before the request reaches the method and logs the method and URL. The ContainerResponseFilter part runs after the method is done and logs the HTTP status code returned. This provides complete insight into what's going on in the API without any code duplication.


## Author
## Ganguli Hettiarachchi
