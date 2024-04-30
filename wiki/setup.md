# Bloom-Filter Server Setup

Run this server on a linux machine and make sure the tcp port of 5535 is free

Download the server from [here](https://github.com/Urimk/Bloom-Filter-Server)

 יש לפתוח טרמינל ולהכנס לתקייה Bloom-Filter-Server ולקמפל בעזרת הפקודה

    g++ -o server tcpServer.cpp bloomfilter/Bloom_Filter.cpp hashmanager/Hash_Manager.cpp

לאחר מכן הרצת השרת תהיה בעזרת הרצת הפקודה

    ./server

# NodeJS Server Setup

יש להריץ את השרת לאחר שפתחנו את השרת Bloom-Filter ראשון!

יש להריץ את השרת מאותו מחשב שהשרת Bloom-Filter רץ בו ויש לוודא שפורט tcp 3001 פנוי.

צריך להוריד את השרת [מפה](https://github.com/Urimk/Facebook-Copycat-Server/tree/Stage-4) (הלינק מביא את בראנץ' של שלב 4 של הפרוייקט שמתאים לשאר הוויקי)

יש להתקין mongodb לפני ההרצה

לאחר ההורדה ניתן לשנות את הפרמטרים של הבלום פילטר בעזרת עריכת הקובץ .env שנמצא בשורש הפרוייקט. הקובץ מגיע עם הרצה לדוגמה. המשתנה הראשון מגדיר את הבלום פילטר, והשני הוא רשימה של קישורים לחסימה.

לאחר ההורדה יש לפתוח טרמינל ולהכנס לתקייה Facebook-copycat-server 

צריך להתקין את המודולים הנדרשים בעזרת הרצת

    npm install

ואז להריץ את השרת בעזרת

    node ./server.js

# Webapp Setup

בשביל שהאתר יעבוד השרתים צריכים לפעול, קודם להפעיל את בלום פילטר ואז את השרת נוד.
השרת node מספק את האתר בגישה לכתובת http://localhost:3001 אבל אם רוצים להריץ את האתר מהפרוייקט react שלו צריך להוריד [מפה](https://github.com/Urimk/Facebook-Copycat-Web/tree/Stage-4) (הלינק של בראנץ' של שלב 4 שמתאים לשאר הוויקי)

לאחר ההורדה יש לפתוח טרמינל ולהכנס לנתיב

    Facebook-Copycat-Web/fb-web

ואז להתקין את המודולים הנדרשים בעזרת הרצת

    npm install

ואז להריץ את העמוד בעזרת

    npm start

# Android App Setup

בשביל שהאפליקציה תעבוד השרתים צריכים לפעול, קודם להפעיל את בלום פילטר ואז את השרת נוד.
כדי להריץ את האפליקציה יש להוריד את הפרוייקט [מפה]() (הלינק מביא בראנץ' של שלב 4 שמתאים לשאר הפרוייקט)

לפתוח את android studio על התקייה

    Facebook-Copycat-App

ולהריץ עם אימולטור בגירסת API 34.