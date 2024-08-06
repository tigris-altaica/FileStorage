# File Storage #
Android application that stores files in encrypted form. The application creates a 'crypt' directory on the user's SD card. When the application starts, it encrypts all unencrypted files in this directory. The application also have functionality to decrypt a specific file and save it to 'decrypt' directory.
The application is protected with a password, which can be changed in the application settings. Uses AES for file encryption. All cryptographic operations are implemented in a shared library and imported using NDK.
