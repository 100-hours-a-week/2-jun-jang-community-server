# 카카오테브 부트캠프 간단한 커뮤니티 프로젝트(jun.jang)
- - -

<h1> Back-end 소개</h1>

- - -

* 서로의 일상을 공유하거나 하고 싶은 말을 하는 커뮤니티 개인 프로젝트입니다.
* Spring boot로 서버를 구현했고, MySql로 db를 사용했습니다.
* MVC 아키텍처로 구현하였으며, 도메인형 패키지 구조로 설계하고 구현했습니다.

<h1> 개발 인원 및 기간 </h1>

 * 개발 기간 : 2025-02-23 ~ 2025-03-29
 * 개발 인원 : 프론트엔드 / 백엔드 1명 (본인)

<h1> 사용 기술 및 tools</h1>

 * MySQL
 * Spring boot
 * Ec2
 * Docker
 * Jacoco

<h1> 폴더 구조</h1>
<details>
  <summary>폴더 구조 보기/숨기기</summary>
  <div markdown="1">

```
├─.github
│  └─workflows
├─.gradle
│  ├─8.12.1
│  │  ├─checksums
│  │  ├─executionHistory
│  │  ├─expanded
│  │  ├─fileChanges
│  │  ├─fileHashes
│  │  └─vcsMetadata
│  ├─buildOutputCleanup
│  └─vcs-1
├─.idea
├─build
│  ├─reports
│  │  └─problems
│  └─tmp
│      └─compileJava
├─gradle
│  └─wrapper
└─src
    ├─main
    │  └─java
    │      └─community
    │          ├─Api
    │          │  ├─Health
    │          │  │  └─Controller
    │          │  ├─Image
    │          │  │  ├─Controller
    │          │  │  ├─Dtos
    │          │  │  └─Service
    │          │  ├─Post
    │          │  │  ├─Controller
    │          │  │  ├─Converter
    │          │  │  ├─Dtos
    │          │  │  └─Service
    │          │  └─User
    │          │      ├─Controller
    │          │      ├─Converter
    │          │      ├─Dtos
    │          │      └─Service
    │          ├─Common
    │          │  └─Enums
    │          ├─Config
    │          ├─Exception
    │          │  ├─JwtException
    │          │  ├─PostException
    │          │  └─UserException
    │          ├─Model
    │          ├─Repository
    │          ├─Security
    │          └─Util
    └─test
        └─java
            └─community
                └─Api
                    ├─Image
                    │  ├─Controller
                    │  └─Service
                    ├─Post
                    │  ├─Controller
                    │  ├─Repository
                    │  └─Service
                    └─User
                        ├─Controller
                        ├─Repository
                        └─Service
```
</div>
</details>