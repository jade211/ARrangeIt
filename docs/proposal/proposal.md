# School of Computing &mdash; Year 4 Project Proposal Form

## SECTION A

|                     |                   |
|---------------------|-------------------|
|Project Title:       | ARrangeIt         |
|Student 1 Name:      | Jade Hudson       |
|Student 1 ID:        | 21706905          |
|Student 2 Name:      | Sruthi Santhosh   |
|Student 2 ID:        | 21377986          |
|Project Supervisor:  | Hyowon Lee        |


## SECTION B

### Introduction

> Our project will focus on developing an augmented reality (AR) - based mobile application for home decor. This app will allow users to visualise furniture and decorative items in their living spaces in order to get a feeling for how the item(s) will look prior to making a purchase. By using AR technology, users can view virtual 3D models of furniture overlaid onto their real-world environment through their smartphone camera. This will help users to make an informed decision about how furniture will look and fit in their homes allowing them to confidently buy the item knowing they made a correct choice.

### Outline

> The proposed project is an AR android app that enables users to browse a catalogue of furniture, place virtual furniture items in their physical space using their phones camera, and save the arrangement for future reference. The app will allow users to adjust the size and orientation of the virtual items and explore different styles and colours to suit their preferences. Key features will include furniture catalogue integration, room scanning, object scaling, and saving configurations.

### Background

> The idea for this project came from the need for a more optimised, interactive shopping experience for home decor. Traditional shopping for furniture often leaves customers uncertain about how items will look or fit in their homes. AR technology bridges this gap by allowing users to visualise furniture in real-time, at scale and in their own spaces.
>
>This app is inspired by existing AR home design tools, such as the IKEA home design feature. Through our experience with this feature, we have found it lacks many optimisations and have identified several areas that could be improved. Firstly, it's limited to Apple devices, restricting access for many users. Secondly, the usability of the feature could be enhanced as the scaling is difficult to use. Introducing helper buttons could streamline the scaling process to ensure the user's scaling preference is met. Additionally, we noticed that the furniture placing feature does not account for existing objects in the space. Our app aims to target these optimisations and also allow users to place multiple pieces of furniture to ensure the room can be fully styled, which is not available on the IKEA app.
>
>We’ve also drawn inspiration from other AR tools, such as Specsavers' online feature that allows users to try on glasses virtually before purchasing. The combination of both of these inspirations led to the development of this idea.


### Achievements

> The project will provide several functionalities:
> - Real-time AR visualisation of 3D furniture models in users’ rooms.
> - Ability to browse and filter furniture items in a catalogue based on item, colour, material, and price.
> - Object scaling and rotation for accurate placement of items in a room.
> - Customisation of furniture finishes and the ability to save room configurations.
> - An AR measurement tool to help users measure the dimensions of their rooms or furniture.
>
> The primary users will be homeowners, renters, interior designers, or anyone looking to furnish or redecorate a space.


### Justification

> From researching into this idea we have found that AR can significantly improve decision-making in retail by offering an immersive experience where users can test products in context. A study by DigitalBridge found that 69% of consumers expect retailers to launch AR apps to allow them to preview items in their own homes. This shows a growing demand for AR technology in retail that this app will be satisfying. This app can be used with other retailer technology as an added feature whereby their catalogue of products can be viewed by consumers in real-time.
>
> Additionally, a study from Harvard Business Review highlights that augmented reality increases consumer confidence and engagement during the shopping process. This directly correlates with improved decision-making and higher customer satisfaction, making AR an important tool for retailers​.
>
> Users planning to redecorate or furnish their homes often struggle to visualise how different furniture pieces will fit or look in their spaces. This app allows them to preview items placed in their home in real-time before making any purchase decisions, reducing uncertainty. It’s particularly helpful during times when in-store visits are difficult or inconvenient such as during busy schedules or even periods like the COVID-19 pandemic, in which stores were closed and in-store perusal was undoable.
>
> The app can be used in any living space - homes, offices, or even outdoor settings. It's ideal for those furnishing a new home, redecorating an existing space, or designing rooms for specific functions, such as home offices, playrooms, or studios. It can be particularly beneficial for use in rooms where space is limited, helping users maximise room layouts and avoid purchasing items that are too large or unsuitable.
>
> By overlaying virtual 3D furniture models in the user's real-world environment, the app eliminates the guesswork of fitting and styling. Furthermore, the ability to scale, rotate, and place multiple items in the same scene provides a realistic and practical approach to room design.

Sources:
- https://www.diyweek.net/retailers-have-up-to-six-months-to-develop-ar-apps-or-risk-losing-customers
- https://hbr.org/2022/03/how-augmented-reality-can-and-cant-help-your-brand


### Programming language(s)

> - Java for the Android app development.
> - XML for designing user interface layouts in Android Studio
> - C# for developing the AR experience in Unity (optional depending on time constraints).

### Programming tools / Tech stack

> - Android Studio: Provides tools for coding, testing, and debugging the app. It also includes the Android SDK necessary for building the Android application.
> - ARCore: This framework enables features like environmental understanding, motion tracking and light estimation.
> - Unity (optional depending on time constraints): This will help to create complex interactions with 3D models.
> - Firebase: Cloud-based backend service that provides real-time database capabilities, user authentication, and cloud storage (this will be used to store and manage the furniture catalogue and user preferences / saving capabilities).
> - GitLab: For version control.
> - JUnit: Will allow for unit testing.
> - Espresso: Will allow for user interface testing.
> - Figma: For creating mock-up designs of the applications user interface.
>
> Websites offering free to use 3D Models that we will use in creating an example furniture catalogue include:
> - Sketchfab
> - TurboSquid
> - Free3D
> - CGTrader
> - Blend Swap



### Hardware

> - Android Device: The device we will be using for development and testing is the Google Pixel 6a
> - USB Cable: A standard USB cable to connect the device to the development computer for testing and debugging the app.
> - Computer: A computer capable of running Android Studio and Unity. It should have at least 8 GB of RAM and a good processor to ensure smooth development and testing.


### Learning Challenges

> ARCore
> - Understanding how to implement augmented reality functionalities, including plane detection, object placement, and scaling in real-world environments. 
> - Learning how ARCore interacts with the camera and sensors of the Android device.
>
> Android Studio
> - Learning its features for code editing, debugging, and testing.
> - Learning how to work with XML for layout design
> - Understanding Gradle for project dependencies
> - Understanding Android Studio Emulator and physical device deployment for testing.
>
> Firebase
> - Understanding how to set up Firebase and how to integrate it into the Android app
> - Managing user data and backend functionality
>
> 3D Model Integration
> - Learning how to import and manipulate pre-made 3D models from various sources for use in your app. This will involve understanding different 3D file formats and how to adjust models within the Android development environment.
>
> Unity (optional and dependant on time constraints)
> - C#
>
> JUnit and Espresso
> - Creating tests to ensure the functionality of the application.



### Breakdown of work

#### Jade Hudson
> - **ARCore Integration & Camera Setup:** Integrate the ARCore framework into the app, setting up real-time camera functionality and implementing the initial AR session that handles real-time plane detection and object placement.
>
> - **UI/UX Design and Front-End Development:** Design and code the app’s user interface, focusing on the layout of catalogue browsing, furniture selection and AR furniture placement buttons. This will mainly be done using Figma to design app mockup prototypes and XML in Android Studio for designing the user interface.
>
> - **Authentication and Backend Integration:** Implement user authentication using Firebase for the secure login feature. This will involve setting up Firebase for managing user authentication and coding the logic to allow users to log in, save preferences and retrieve their saved room configurations.
>
> - **Furniture Catalogue Integration:** Select and integreate the 3D furniture models into the catalogue. This will involve sourcing 3D models and then using Firebase to store the models in the catalogue, ensuring the app can query and filter the models based on colour, size etc.

#### Sruthi Santhosh

> -  **Implement Object Placement and Movement Functionality:** Code basic object placement functionality whereby users can select a point on the plane and place, rotate and scale the furniture. This will be done using ARCore.
>
> -  **3D Model Loading and Manipulation:** Handle the technical aspects of importing and rendering the 3D models into the AR space. This will be done using Android Studio with ARCore for rendering the models into the app.
>
> - **Advanced Interaction Buttons (Move Forward/Backward):** Implement additional interaction buttons that allow users to move objects forwards and backward within the AR environment.
>
> - **Testing and Debugging:** Conduct testing of all app features including user, unit and UI testing. The main testing will be done using JUnit and Espresso.


