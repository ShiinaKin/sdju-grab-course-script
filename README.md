## Env

JRE21+

## Run

1. run `java -jar xxx.jar`
2. modify config
   ```yaml
   ---
   authorization: "authorization"  # find it from req header, pay attention to the period, as i write this, it's valid for 12h
   studentId: 73211
   categoryAndKeyword:  # don't modify category name
     通识选修:
       first: 1  # need grab course cnt
       second:   # keywords of course name
       - "智慧树"
       - "尔雅"
     美育英语:
       first: 0
       second: []
     培养方案:
       first: 0
       second: []
     体育四史:
       first: 0
       second: []
   ```
3. refresh config
4. grab