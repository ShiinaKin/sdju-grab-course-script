## Env

JRE21+

## Run

1. run `java -jar xxx.jar`
2. modify config
   ```yaml
   ---
   authorization: "authorization"  # Open the DeveloperTools(F12) and click `Student Course Select`, find /api/v1/student/course-select/open-turns/123456 request, 
                                   # find authorization from it req header, pay attention to the period, as I write this, it's valid for 12h
   cookie: "cookie"  # same as above
   studentId: 123456  # same place as above, find it at the end of uri
   categoryConfigMap:
     通识选修:
     - code: null
       name: "智慧树"
       teacher: null
       minCredits: 2.0
       credits: null
     - code: null
       name: "智慧树"
       teacher: null
       minCredits: 2.0
       credits: null
     - code: null
       name: "尔雅"
       teacher: null
       minCredits: 2.0
       credits: null
     美育英语: []
     培养方案:
     - code: "(2024-2025-1)-000000A1-01"  # highest priority
       name: null
       teacher: null
       minCredits: null
       credits: null  # priority is higher than minCredits
     体育四史: []
   ```
3. refresh config
4. grab