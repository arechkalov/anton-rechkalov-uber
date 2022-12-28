#Backend system which supports drivers mobile app.
```
user service
payment-service
```
To start both applications - exectute
execute mvn clean install to build the project

1. mvn spring-boot:run inside the project
2. from IDE:
    UberUserApplication.class
    UberPaymentApplication.class

user service swagger-ui running locally: http://localhost:8080/swagger-ui
payment-service swagger-ui running locally: http://localhost:8081/swagger-ui
***
Both services are connected to two databases remotely on google cloud platform using secret keys.
***

## Authenticate predefined user 
1. Make authentication call:
POST /api/v1/authentication
    ```json
    {
        "password": "password",
        "phoneNumber": "12345678"
    }
   ```
2. Obtain token from response:
   ```json
   {
    "jwt": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxMjM0NTY3OCIsImV4cCI6MTYxOTQ4MTg2MywiaWF0IjoxNjE5NDQ1ODYzfQ.ZF92tVzmQSlAENytXS-Evxq6haH662OaPdlguCgy-Ys"
   }
   ```
3. Copy-paste it to pop up window on swagger-ui (alternatively you can add it to Authorization header if making calls from Postman for example)

![img.png](img.png)
---------------------------

## Simulating the rider start searching a trip
1. Add trip by 
   POST /api/v1/trips
 ```json
{
  "customerAccountNumber": "321",
  "endLocation": {
    "latitude": 1223424.123,
    "longitude": 12341514.54
  },
  "initialPrice": 10,
  "paymentType": "MASTERCARD",
  "reward": 2,
  "startLocation": {
    "latitude": 1212.122,
    "longitude": 1212.12
  }
}
``` 

resposne will return the trip Id
   ```json
   {
    "tripId": 1
   }
   ```
2. Driver will acquire the trip by given id:
   by PUT /api/v1/trips/{tripId}/acquisition 

the callback url will be sent to the payment-service for authorization and it will return staus with trip correlationId either success or failure in a separate call to uber-user service

## Trip Confirmation
when trip ends this endpoint will be triggered whith the given trip id:
PUT /api/v1/trips/{tripId}/confirmation

the callback url will be sent to the payment-service for confirmation and it will return status with trip correlationId either success or failure in a separate call to uber-user service

------------------------------

## Registration of user

Registration is done in two steps:
1 POST /api/v1/registration with 

```json
    {
      "firstName": "asdf",
      "lastName": "asdf",
      "password": "asdf",
      "phoneNumber": "asdfsdf"
    }
```
it will respond with token we can use it to send to an email to confirm registration
```text
    9b3e659b-00b4-434f-a8cc-88da8841bb63
```
2 Confirm with token on 
 GET /api/v1/registration/confirmation

it will enable user to access other endpoints.

## Register a vehicle for driver endpoint
        PUT /api/v1/drivers/vehicles
```json
{
  "colour": "White",
  "name": "Dacia",
  "registrationNumber": "AG 899 SFA"
}
```


GET /api/v1/drivers/{phoneNumber}/trips/analytics - set of analitycs endpoint for current user  trips (daily average cost, total trips time per day, total trips price per day, averge cost per trip overall)

GET /api/v1/trips - gets all assigned trips for current user

GET /api/v1/drivers/{phoneNumber/statistics - gets all statistics for the driver.


