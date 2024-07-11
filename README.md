## Env

JRE21+

## Run

1. run `java -jar xxx.jar`
2. modify config
   ```yaml
   ---
   authorization: "authorization"  # find it from req header, pay attention to the period, as i write this, it's valid for 12h
   studentId: 73211
   categoryConfigMap:
     通识选修:
       count: 1  # need grab course cnt
       minCredits: 2.0  # min credits
       keywords:  # keywords of course name
       - "智慧树"
       - "尔雅"
     美育英语:
       count: 0
       minCredits: 2.0
       keywords: []
     培养方案:
       count: 0
       minCredits: 2.0
       keywords: []
     体育四史:
       count: 0
       minCredits: 2.0
       keywords: []
   ```
3. refresh config
4. grab