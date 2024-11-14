# EPIC Earth GIF Project

## Overview
This project aims to create an online service that generates animated GIFs of Earth using image data from NASA’s EPIC API. The service will use a machine learning model for image interpolation to create smooth animations.

## Functional Requirements
- **Data Retrieval**: Interface with the EPIC API to download images.
- **Processing**: Use machine learning to interpolate additional frames.
- **GIF Generation**: Create animated GIFs from processed images.
- **Database Management**: Store raw and processed images, GIFs, and metadata.
- **User Interface**: Provide a web-based interface for users to view and download animations.

## System Design
- **User Interface**: HTML/CSS/JavaScript with dynamic elements.
- **Backend Service Layer**: 
  - Data Retrieval: Interfacing with NASA’s EPIC API.
  - Image Interpolation: Machine learning model as a microservice.
  - GIF Generation: Dedicated module triggered after interpolation.
- **Data Management**: 
  - Database Schema: Tables for raw images, interpolated images, GIFs, and metadata.
  - Data Access Objects (DAO): Clean API for business logic.

## Non-Functional Requirements
- **Scalability**: Cloud deployment for scaling.
- **Security**: Secure access to the NASA API and database storage.
- **Performance**: Asynchronous processing for image interpolation and GIF generation.
- **Reliability**: Retry mechanisms in data retrieval and processing.

## Technologies
- **Backend**: Java EE, Spring Boot, Spring MVC
- **Frontend**: HTML, CSS, JavaScript
- **Data Processing**: Spark for ML-based image interpolation
- **Deployment**: Docker for containerization

## Getting Started
1. Clone the repository.
2. Set up the environment variables in the `.env` file.
3. Follow the instructions in the documentation to set up the backend and frontend components.

## License
This project is licensed under the MIT License.