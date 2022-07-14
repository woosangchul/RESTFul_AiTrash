# RESTFul_AiTrash
[서비스 URL] http://ec2-3-37-50-195.ap-northeast-2.compute.amazonaws.com:8080/main
<br>  
![이미지](https://user-images.githubusercontent.com/40749537/160983482-ec0b0254-6966-4a07-b2f4-a20c24a2ed8b.png)

### 프로젝트 소개

[텐서플로를 사용한 인공지능 쓰레기통](https://github.com/woosangchul/trash_project/) 프로젝트의 백엔드버전으로 AWS와 Spring Boot를 공부해보고 싶어서 진행한 프로젝트.  
Spring Boot로 백엔드를 구성하고 Bootstrap으로 모바일과 웹 환경 대응해서 사용자는 이미지를 업로드하면 해당 이미지가 어떤 쓰레기인지 예측한 결과값을 볼 수 있다.

**기존 프로젝트와 차별점**  
기존에는 RESTFul API만 사용해서 이미지를 딥러닝 서버로 보내면 어떤 쓰레기인지만 예측해서 결과 알려주는 방식. 어떤 이미지를 서버로 전송했는지 알 수 없었다.
