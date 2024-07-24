## Env

JRE21+

## Run

1. run `java -jar xxx.jar`
2. modify config
   ```yaml
   ---
   authorization: "authorization"  # find it from req header, pay attention to the period, as i write this, it's valid for 12h
   studentId: -1
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