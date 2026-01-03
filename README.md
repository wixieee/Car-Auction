# Car Auction Platform

This project is a comprehensive car auction platform built with Java and Spring Boot. It provides a secure, real-time, and user-friendly environment for buying and selling cars. The platform features a RESTful API for all auction operations, real-time bidding with WebSocket updates, and robust image handling with Google Cloud Storage. The entire application is containerized using Docker for streamlined deployment and scalability.

## Key Features

- **User Management**: Secure user registration and authentication using JWT and Google OAuth2.
- **Auction Lifecycle Management**: Full control over the auction process, including lot creation, admin review and approval, and cancellation.
- **Real-Time Bidding**: Engage in auctions with real-time bid placement and instant updates pushed to all participants via WebSockets.
- **Anti-Sniping**: Fair bidding is ensured with an anti-sniping feature that extends the auction time if a bid is placed in the final minutes.
- **Payment and Fund Management**: Securely manage user funds with a wallet system that handles deposits, fund freezing during bids, and seamless fund transfers upon auction completion.
- **Image Handling**: Upload and manage car images, which are securely stored in Google Cloud Storage.
- **Advanced Filtering**: Easily find lots using a variety of filter options.

## Technologies Used

- **Backend**: Java 17, Spring Boot
- **Database**: PostgreSQL
- **Authentication**: Spring Security, JWT, Google OAuth2
- **Real-Time Communication**: Spring WebSocket
- **Image Storage**: Google Cloud Storage
- **Deployment**: Docker, Docker Compose
- **Testing**: JUnit, Mockito, Testcontainers

## Getting Started

To run the project locally, please follow these steps:

### Prerequisites

- Java 17 or later
- Docker and Docker Compose
- A Google Cloud Platform account with a configured project and storage bucket

### Installation

1. **Clone the repository**:
   ```bash
   git clone https://github.com/wixieee/Car-Auction.git
   ```
2. **Configure environment variables**:
   Create a `.env` file in the project's root directory and populate it with the necessary environment variables for the database, JWT, Google OAuth, and Google Cloud Storage.
3. **Run the application**:
   ```bash
   docker-compose up --build
   ```

## API Documentation

Once the application is running, you can access the interactive API documentation at:

[http://localhost:8080/api/swagger-ui.html](http://localhost:8080/swagger-ui.html)
