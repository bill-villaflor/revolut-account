# Account Service

## Running the Application
### Prerequisites
* Java 11.0.2-open
* Git
### Steps
1. Clone git repository ```git clone https://github.com/bill-villaflor/revolut-account.git```
2. Go to cloned repository ```cd revolut-account```
3. Run application ```java -jar releases/account-service-0.1.0.jar```
4. Check if application is up at [http://localhost:8080/health](http://localhost:8080/health)
5. Swagger API documentation yaml can be viewed at [http://localhost:8080/swagger/account-service-0.1.0.yml](http://localhost:8080/swagger/account-service-0.1.0.yml) 
6. Paste the yaml to any online swagger editor like [https://editor.swagger.io/](https://editor.swagger.io/) for better viewing

## Testing the Application
### Prerequisites
* Python3 3.6.5
* Pip3 19.1.1
* Application is running at [http://localhost:8080](http://localhost:8080)
### Steps
1. Create python virtual environment ```python3 -m venv account-test-venv```
2. Run environment ```source account-test-venv/bin/activate```
3. Go to test directory ```cd revolut-account/account-test```
4. Install environment dependencies ```pip3 install -r requirements.txt```
5. Run functional tests ```behave```
6. Application specifications ending in *.feature can be viewed at ```revolut-account/account-test/features```

## Building the Application
### Prerequisites
* Java 11.0.2-open
* Gradle 5.4.1
* Micronaut 1.1.3
### Steps
1. Go to application directory ```cd revolut-account/account-service```
2. Build application ```./gradlew clean build```
