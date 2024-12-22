# File Server with Java Spring Boot

## Overview
This is a Spring Boot application that functions as a file server. It provides RESTful endpoints to manage files, including functionalities to list, upload, download, and delete files. The application uses Amazon S3 for file storage, offering scalability, durability, and integration with AWS services.

---

## Features
- **List Files**: Retrieve a list of all uploaded files.
- **Upload Files**: Upload files to the server.
- **Download Files**: Download files stored on the server by their filename.
- **Delete Files**: Remove specific files from the server.

---

## Technologies Used
- **Java**: Core programming language.
- **Spring Boot**: Framework for building RESTful APIs.
- **Maven**: Dependency and build management.
- **Spring Web**: Provides RESTful API capabilities.
- **Spring Boot Starter Test**: For unit and integration testing.
- **Amazon S3**: Cloud storage backend for file storage.

---

## Setup and Installation

### Prerequisites
1. Java 17 or higher.
2. Maven 3.8 or higher.
3. AWS account with S3 bucket set up.
4. AWS credentials configured (e.g., `~/.aws/credentials`).

### Installation Steps
1. Clone this repository:
   ```bash
   git clone https://github.com/bfluharty/FileServer.git
   cd fileserver