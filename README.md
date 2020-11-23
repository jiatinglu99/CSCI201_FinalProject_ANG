# CS270-Final-Project

## Deployment Guide
1. Unzip the project and put it somewhere on the disk. 

2. Configure Java Runtime Environment

3. Open project in Eclipse, choosing the folder just unzipped

4. On the server side machine, run register.sql in the project folder, and apply it to a MySQL instance. (the user and password should be “root”, or can be changed accordingly in Database.java) Change the jdbcUrl in Database.java to "jdbc:mysql://localhost:3306/your_database_name" (The default name is fpdatabase). 

5. In server.properties, configure the server to an arbitrary port by altering the integer value. This is defaulted to 5677. 

6. For server, change the server.properties to change the port used by the application (default is 5677). For client, change the client.properties to change the desired address and port (default is localhost::5677). 

7. After the necessary configurations, start running the program with the following steps

  8. On server, run the main() in Server.java to create a server.
  9. On client machine(s), run main() in GUI.java to start actual playing.
