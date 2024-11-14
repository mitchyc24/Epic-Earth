
# CP-630 Enterprise Computing Project Design Document

## 1. Project Overview

### **Objective**
To create an online service that generates animated **GIFs** of Earth using image data from **NASA’s EPIC API**, with image interpolation via a **machine learning model** for smooth animation.

### **Functional Requirements**
- **Data Retrieval**: Interface with the **EPIC API** to request and download images.
- **Processing**: Use machine learning to interpolate additional frames.
- **GIF Generation**: Create an animated **GIF** from the processed images.
- **Database Management**: Store raw and processed images, **GIFs**, and metadata.
- **User Interface**: Provide an accessible web-based interface for users to view and download animations.

## 2. Enterprise Components and Frameworks

### **Backend Components**
- **Java EE Platform**: Core framework, leveraging **Java EE** for creating a robust, scalable, and secure backend.
- **Session Beans (Lesson 2)**: Define the business logic for handling requests and orchestrating data retrieval, processing, and storage.
- **Message-Driven Beans (Lesson 3)**: Use for asynchronous processing of image data to ensure smooth performance.

### **Data Persistence**
- **JPA and Entity Beans (Lesson 4)**: Define and manage entities representing images, metadata, and **GIFs**.
- **Database**: Implement an Object-Relational Mapping for storing and querying data effectively using **Java Persistence Query Language (JPQL)**.

### **Web Tier Components**
- **Java Servlets and JSP (Lesson 5)**: Handle **HTTP** requests and dynamic content for the user interface.
- **RESTful API with JAX-RS (Lesson 5)**: Create endpoints for retrieving and displaying images, metadata, and **GIFs** to the web interface.

### **Client Tier Computing**
- **JavaScript (Lesson 6)**: Enhance user interactivity on the frontend, and use **WebSocket** if real-time updates or notifications are needed.

### **Spring Framework**
- **Spring Boot (Lesson 7)**: Simplify configuration and deployment of the application.
- **Spring MVC**: Manage the application’s **MVC architecture**, handling client requests and responses.

### **Cloud Deployment and Big Data**
- **Containerization (Lesson 9)**: Dockerize the application for easy cloud deployment.
- **Hadoop or Spark for Image Processing (Lesson 12)**: Consider using **Spark** for **ML-based** image interpolation and efficient data processing.

## 3. System Design and Architecture

### **User Interface**
- **Web UI**: **HTML/CSS/JavaScript** with dynamic elements managed by JavaScript libraries.
- **RESTful API**: Serve data and images, with endpoints structured to support search, filter, and display functionality.

### **Backend Service Layer**
- **Data Retrieval**: Interfacing with NASA’s **EPIC API**.
- **Image Interpolation**: A machine learning model implemented as a microservice.
- **GIF Generation**: A dedicated module, triggered after interpolation completes.

### **Data Management**
- **Database Schema**: Tables for raw images, interpolated images, **GIFs**, and metadata.
- **Data Access Objects (DAO)**: Encapsulate access to data and provide a clean API for business logic.

## 4. Non-Functional Requirements
- **Scalability**: Leverage cloud deployment for scaling.
- **Security**: Implement secure access to the NASA API and secure database storage.
- **Performance**: Use asynchronous processing for image interpolation and GIF generation.
- **Reliability**: Build retry mechanisms in data retrieval and processing.

---
*This document outlines the design for a project that will leverage various enterprise computing principles to create a visually engaging, interactive service.*
