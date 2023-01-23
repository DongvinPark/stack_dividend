# Zerobase 백엔드스쿨 5기 3번째 개인프로젝트 - 주식 배당금 API

## 특이사항
- [MemberEntity.java 클래스](https://github.com/DongvinPark/stock_dividend/blob/main/src/main/java/com/dayone/model/constants/MemberEntity.java)를 구현하는 과정에서 강의의 구현과는 다른 방식으로 처리했습니다.
- 스트링 타입의 리스트 roles이라고 코딩했더니 'Basic' attribute type should not be a container 라는 에러가 발생했습니다.
- 무슨 시도를 해도 List, Set, 등의 컨테이너를 이 엔티티 클래스가 받아들이지를 못해서 할 수 없이 아래의 방법으로 처리했습니다.
- 리스트 스트링이 아니라 그냥 스트링으로 롤을 정의하되, "ROLE_READ,ROLE_WRITE" 이런 식으로 쉼표로 파싱해서 다수의 롤들을 하나의 스트링 안에 집어넣게 하고, getAuthorities()메서드에서는 쉼표를 기준으로 파싱된 롤 스트링들을 전달받게 만들어서 해결했습니다.

## 작동 이미지
- 로그파일 생성 및 출력 결과입니다. IDE의 프로젝트 창에서 mylog.log 파일이 생성된 것을 볼 수 있습니다.
![9CC39FB8-062B-4252-92FA-F4D673EFB692](https://user-images.githubusercontent.com/99060708/214012309-979ccbfe-a513-4e4b-85db-fd00f4e1b1fe.jpeg)