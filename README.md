
![Logo](https://i.postimg.cc/h4YNkc9X/temp-Image-MSg-T81.jpg)


# 𝐋𝐢𝐧𝐤𝐞𝐝𝐄𝐝𝐠𝐞: 𝐔𝐧𝐥𝐨𝐜𝐤 𝐭𝐡𝐞 𝐅𝐮𝐭𝐮𝐫𝐞 𝐨𝐟 𝐈𝐧𝐭𝐞𝐫𝐯𝐢𝐞𝐰𝐬

**LinkedEdge** takes your interview preparation to the next level by harnessing AI-driven question generation. What sets this application apart? The system generates interview questions based on the information gleaned from your LinkedIn account, ensuring that your practice is both relevant and insightful.



## Features

- **AI-Generated Interview Questions**: LinkedEdge's AI algorithms generate a diverse set of interview questions that challenge and enhance your interview skills. These questions are personalized to you through the data sourced from your LinkedIn profile.

- **Customized Queries**: If you have specific needs or a particular role in mind, you can request tailored interview questions. LinkedEdge fine-tunes its question generation to meet your requirements.

- **Answer Specific Queries**:In addition to generating questions, LinkedEdge can provide the best possible answers, offering real-time feedback and guidance to help you craft impressive responses.

- **Personalized Job Preparation by LinkedIn Job Post**: LinkedEdge creates personalized job preparation content using your LinkedIn profile and LinkedIn Job post, ensuring you're fully equipped for success in your specific job interviews.

- **Robust Authorization System**: LinkedEdge ensures your account's security and ease of use with a robust authentication system, including user registration, email confirmation, multi-factor authentication, and password recovery. It leverages JWT for enhanced data security and offers a custom logout process for added flexibility and protection.


  


## Prerequisites

Before you start using DocConnect, ensure you have the following prerequisites in place:

- **Java Development Kit (JDK) 17 or newer:** If you don't have Java installed or need to update your current version, you can download JDK 17 from the official Oracle website or use a compatible distribution like OpenJDK.

    - [Install Oracle JDK 17](https://www.oracle.com/java/technologies/downloads/)

- **MySQL Database:** Make sure you have MySQL installed on your machine. If not, you can download MySQL from the official website or use a package manager like Homebrew on macOS.

  - [Install MySQL](https://dev.mysql.com/downloads/)

- **SMTP Email**: If you don't have SMTP email configured or need to update your current settings, you can obtain the required SMTP server details from your email provider.

  - [More about SMTP Emails](https://www.baeldung.com/spring-email)

- **Lix Account Required for API Access**: You must have a Lix account and obtain an API key. This API key is essential for accessing LinkedIn profiles and job information through the application. Please ensure you have this key to unlock the full functionality of the application.
  
   - [More about Lix](https://lix-it.com/)

- **OpenAI Account Required for API Access**: It's imperative to have an OpenAI account and obtain an API key. This key is the backbone of various validation processes and powers all of the application's core functionalities. Make sure to secure your API key to enable the application's complete range of features.

   - [More about OpenAI](https://platform.openai.com/)

- **ApiLayer Account for BadWordsAPI Access**: It's essential to possess an ApiLayer account and acquire a BadWordsAPI key. This key is instrumental in detecting and filtering out offensive language, ensuring a clean and respectful environment. Obtain your BadWordsAPI key to enable the application's profanity-checking functionality.

  - [More about ApiLayer](https://apilayer.com)
  - [More about BadWordsAPI](https://apilayer.com/marketplace/bad_words-api?live_demo=show)

- **RSA Public and Private Keys for JWT Tokens**: For the security of JWT tokens within the application, it's crucial to generate both RSA Public and Private keys. Neglecting this step may expose vulnerabilities. Protect your JWT tokens by following this important process.


## Environment Variables

In order to successfully run the application, you must configure the following environment variables in your application.properties

- `DATABASE_URL`: This variable specifies the URL or connection string for your database.

- `DATABASE_NAME`: It defines the name of the database you intend to use.

- `DATABASE_USERNAME`: The username required to access and interact with your database.

- `DATABASE_PASSWORD`: The password associated with the specified username.

- `EMAIL_USERNAME`: Your email account's username for sending notifications and communications.

- `EMAIL_PASSWORD`: The password for your email account.

- `LIX_API`: This variable is for the API key associated with your Lix account, enabling LinkedIn profile and job retrieval.

- `OPEN_AI_API`: Here, you will provide the API key from your OpenAI account, which is vital for various application validations and functionalities.

- `BAD_WORDS_API`: This variable corresponds to the API key for the BadWordsAPI, used for profanity checks in text.




## Installation

To get started with DocConnect, clone this repository to your local machine using the following command:

  ```bash
   git clone https://github.com/parunev/LinkedEdge.git
```

- Open a terminal or command prompt in the project's root directory or navigate to it using the following command:
```
cd LinkedEdge
```

- Build the project using Maven:

```
./mvnw clean install
```

- Run the Spring Boot application:

```
./mvnw spring-boot:run
```


    
## Database: Optimized for Performance and Efficiency

The database structure is meticulously organized to minimize redundancy, improve data integrity, and streamline data retrieval. 

![LinkedEdge Database](https://i.postimg.cc/7httDMGv/temp-Imagepn-L3-AY.jpg)

## Comprehensive Documentation

The documentation is not only thorough but also conveniently accessible. Documentation through both Swagger for API details and JavaDoc within classes to ensure a comprehensive understanding of the system.

I've gone the extra mile to ensure that all the information you need is at your fingertips. Should you have any questions or need further clarification, don't hesitate to reach out.

```http
  Accessible via: localhost:8080/swagger-ui/index.html/
```
![LinkedEdge Documentation](https://i.postimg.cc/mrdm7yjP/temp-Imagety1-EFu.jpg)


## Logging Levels

- **DEBUG**: Detailed information for debugging and troubleshooting. Use this level when investigating issues or monitoring specific application behaviors.

- **INFO**: General information about the application's operation. This level provides a high-level overview of key events and processes.

- **WARN**: Warnings about potential issues or irregularities that do not necessarily indicate a problem. These messages are essential for proactive monitoring.

- **ERROR**: Indicates errors or exceptional conditions that require immediate attention. These messages are crucial for identifying and resolving critical issues.
## License

[MIT License with Attribution Clause](https://github.com/parunev/LinkedEdge/blob/main/LICENSE)

### Under this MIT License with Attribution Clause, you are granted the following permissions

 - **Usage**: You are allowed to use the software for any purpose, whether it's for personal or commercial use.
 - **Modification**: You can modify and adapt the software to suit your specific needs.
 - **Distribution**: You can distribute the software to others, either in its original form or as modified by you.
 - **Sublicensing**: You can sublicense the software to others, which means allowing others to use, modify, and distribute it as well.
 - **Sale**: You can sell copies of the software.

### License Conditions:

 - **Attribution**: You are required to include the original copyright notice and the permission notice in all copies or substantial portions of the software. This means acknowledging the original author, Martin Parunev, and recognizing his contributions to the project.
 - **No Warranty**: The software is provided "as is" without any warranty, which means that the author or copyright holder, Martin Parunev, is not responsible for any issues or damages that may arise from the use of the software.




## Support

If you have any questions, encounter issues, or need further assistance, please don't hesitate to reach out. I am here to help you make the most of my application.

#### Contact Information:

You can contact me through the following channels:
 - Email: Reach me via email at [Gmail](mailto:parunev@gmail.com?subject=[LinkedEdge]%20Source%20GitHub)
 - LinkedIn: Feel free to connect with [LinkedIn](www.linkedin.com/in/martin-parunev-49006425b) for professional networking or inquiries.

 I value your experience with LinkedEdge and I'm always open to addressing your queries and concerns. Your satisfaction is my priority, and I'm here to support you in any way I can.

