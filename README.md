# SHA-256
To understanding 'SHA-256' by developing my own faster sha-256 algorithm.

---------------------------------------

### Performance test java.security.MessageDigest VS My SHA256
|Class|Count|Elapsed Time (s)|AVG (s)|Enhanced %|
|:---:|:---:|:---:|:---:|:---:|
|java.security.MessageDigest|1|27.46|27.444|+ 0%|
|"|2|27.408|||
|"|3|27.64|||
|"|4|27.324|||
|"|5|27.389|||
|My SHA256|1|9.749|8.466|+ 69.15%|
|"|2|8.159|||
|"|3|8.085|||
|"|4|8.113|||
|"|5|8.226|||

*Test environment: i7-6700HQ with OpenJDK (build 16+36-2241)  
Elapsed Time and AVG shorter is better, Enhanced % = 100 * (1 - (8.466 / 27.444))*

---------------------------------------

### Code for performance test
[SHA256_Performance_Test.java (Link)](https://github.com/lilllung09/SHA-256/blob/0ed88bf89dd51f5eac1583f51ddaabaa6772976c/src/SHA256_Performance_Test.java)  
_Is there something wrong what I missed?_

---------------------------------------
### Source for Study and Develope (Thanks!)

SHA-256 Hash Algorithm - https://jusths.tistory.com/43?category=796552  
Cryptographic One-Way Hash Functions - https://www.cs.rit.edu/~ark/lectures/onewayhash/onewayhash.shtml  
Secure Hash Standard (SHS) - https://nvlpubs.nist.gov/nistpubs/FIPS/NIST.FIPS.180-4.pdf  

