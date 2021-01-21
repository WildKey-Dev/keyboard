# ideafast-keyboard
Demo [apk]()

## how to compile

 - Download the AOSP source code - [see instructions](https://source.android.com/setup/build/downloading)
 - Download the latest JDK for linux and extract the contents to /<aosp-root>/prebuilts/jdk/jdk8/linux-x86
    - This is necessary due to a bug in the JAVA source that wasn't fixed in the shipped version
 - Clear the directory ```/<aosp-root>/packages/inputmethods/LatinIME```
 - Clone the contents of this repo to ```/<aosp-root>/packages/inputmethods/LatinIME```
    - Should look like ```/<aosp-root>/packages/inputmethods/LatinIME/java/src/...```
 - On a terminal run one by one:
    - ```cd <aosp-root-path>```
    - ```source build/envsetup.sh```
    - ```tapas LatinIME arm64```
    - ```m```
 - The built apk is on ```/<aosp-root>/out/target/product/generic_arm64/system/product/app/LatinIME/LatinIME.apk```
  
## how to sign it
 
  - Download [this](https://github.com/patrickfav/uber-apk-signer/releases) library
  - Run ```java -jar uber-apk-signer-1.1.0.jar -a LatinIME.apk --allowResign --ks <keystore-path> --ksAlias <alias> --ksKeyPass <alias-pass> --ksPass <keystore-pass>```
    - How to create a keystore: ```keytool -genkey -v -keystore my-key.keystore -alias alias_name -keyalg RSA -keysize 2048 -validity 10000```

## how to install
  - Run ```adb install LatinIME-aligned-signed.apk```

## how to use the firebase
  - Create a firebase project
  - Add an android app
    - Fill the package and SHA1 fields
  - Download the ```google-services.json``` file
  - With the content of ```google-services.json``` fill this [example](https://www.notion.so/values-xml-eef5a253e5934a0e9c1848b5756a255d) and create a ```values.xml``` with it inside ```/<aosp-root>/packages/inputmethods/LatinIME/java/res/values/``` 
  - Set the appropriated rules for read and write of the real time data base [(example)](https://www.notion.so/Rules-8cf11adaa63844a6af802a17ad083fd7)
  - An [example](https://www.notion.so/Database-strucutre-4a51b19b3bf843bc9ed6a01285417c0a) on how to structure the DB
