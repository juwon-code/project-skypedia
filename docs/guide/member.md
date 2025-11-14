### 회원 API

#### 심볼 목록
✔️(작업 완료), ⚠️(작업 예정), ❌(검토 필요), 🧑(일반 회원), 🛡️(관리자)

#### API 목록
| Name   | Method | URI                              | Description                 | Done |Auth|
|--------|:------:|----------------------------------|-----------------------------|:----:|:---:|
| 내 프로필 조회 |  GET   | /api/v1/member                   | 현재 로그인한 회원의 프로필을 조회합니다.     |  ✔️  |🧑‍|
| 내 프로필 수정 | PATCH  | /api/v1/member                   | 현재 로그인한 회원의 프로필을 수정합니다.     |  ✔️  |🧑‍|
| 회원 탈퇴  | DELETE | /api/v1/member                   | 현재 로그인한 회원의 탈퇴를 수행합니다.      |  ✔️  |🧑‍|
| 회원 프로필 조회 |  GET   | /api/v1/member/admin/{회원ID}      | ID와 일치하는 회원의 상세 프로필을 조회합니다. |  ✔️  |🛡️|
| 회원 목록 검색 |  GET   | /api/v1/member/admin/search      | 옵션과 키워드로 회원 목록을 검색합니다.      |  ✔️  |🛡️|
| 강제 탈퇴  | DELETE | /api/v1/member/admin/{회원ID}      | ID와 일치하는 회원의 탈퇴를 수행합니다.     |  ✔️  |🛡️|
| 복구 처리  | PATCH  | /api/v1/member/admin/{회원ID}      | ID와 일치하는 회원의 탈퇴를 취소합니다.     |  ✔️  |🛡️|
| 관리자 권한 부여 | PATCH  | /api/v1/member/admin/role/{회원ID} | ID와 일치하는 회원에 관리자 권한을 부여합니다. |  ✔️  |🛡️|
| 관리자 권한 제거 | DELETE | /api/v1/member/admin/role/{회원ID} | ID와 일치하는 회원의 관리자 권한을 제거합니다. |  ✔️  |🛡️|

<details>
<summary>내 프로필 조회</summary>

현재 로그인한 회원의 프로필 데이터를 조회합니다.

- 요청문 예시

```shell
curl -H "Authorization: Bearer AAAAPIuf0L+qfDkMABQ3IJ8heq2m...dbvsiQbPbP1/cxva23n7mQShtfK4pchdk/rc=" \
     -X GET "https://skypedia.com/api/v1/member"
```

- 응답 바디 예시
```json
{
  "message": "내 프로필을 성공적으로 조회했습니다.",
  "result": {
    "nickname": "username1234",
    "email": "username1234@gmail.com",
    "socialType": "GOOGLE",
    "photoUrl": "http://image12345.com/qewfunokvozcxvmapdfas.png",
    "removed": false
  }
}
```

- 에러 메시지 목록

| Situation | Status      | Message                  |
|-----------|-------------|--------------------------|
| 탈퇴한 회원임 | Bad Request | 탈퇴한 회원은 이용할 수 없는 서비스입니다. |
| 인증정보 없음 | Unauthorized | 해당 서비스를 사용하기 위해서는 로그인이 필요합니다. |

</details>

<details>
<summary>내 프로필 수정</summary>

현재 로그인한 회원의 닉네임 및 프로필 사진을 변경합니다.
두 항목 모두 변경하거나, 하나만 선택적으로 변경할 수 있습니다.

- 요청문 예시

```shell
curl -H "Authorization: Bearer AAAAPIuf0L+qfDkMABQ3IJ8heq2m...dbvsiQbPbP1/cxva23n7mQShtfK4pchdk/rc=" \
     -H "Content-Type: application/json" \
     -d '{
      "nickname": "username1234",
      "photoData": {
        "filename": "photo1.jpeg",
        "mediaType": "image/jpeg",
        "fileSize": 1352456
      }
     }'
     -X PATCH "https://skypedia.com/api/v1/member"
```

| Field     | Description                                          |
|-----------|------------------------------------------------------|
| nickname  | 변경하고 싶은 닉네임 값입니다. (Optional)                         |
| photoData | 업로드할 사진의 메타데이터 입니다. (Optional)                       |
| filename  | 확장자를 포함하고 있는 전체 파일명입니다.                              |
| mediaType | 파일의 미디어 타입입니다. (jpg, jpeg, png, gif, bmp, webp, svg) |
| fileSize  | 파일의 크기입니다. 비트 단위이며, 10MB 이하의 크기만을 지원합니다.             |

- 응답 바디 예시
```json
{
  "message": "내 프로필을 성공적으로 업데이트했습니다.",
  "result": {
    "nickname": "username1234",
    "uuid": "9b1deb4d-3b7d-4bad-9bdd-2b0d7b3dcb6d.jpeg",
    "uploadUrl": "https://storage.googleapis.com/bucket/9b1deb4d-3b7d-4bad-9bdd-2b0d7b3dcb6d.jpeg"
  }
}
```

| Field    | Description                                |
|----------|--------------------------------------------|
| nickname | 변경된 새로운 닉네임 입니다.                           |
| uuid | 업로드할 프로필 사진의 UUID와 확장자를 포함하고 있는 전체 파일명입니다. |
| uploadUrl | 파일 업로드 용 일회용 링크입니다.                        |

- 에러 메시지 목록

| Situation     | Status      | Message                  |
|---------------|-------------|--------------------------|
| 인증정보 없음 | Unauthorized | 해당 서비스를 사용하기 위해서는 로그인이 필요합니다. |
| 탈퇴한 회원임 | Bad Request | 탈퇴한 회원은 이용할 수 없는 서비스입니다. |
| 중복된 닉네임 | Conflict | 중복된 닉네임을 사용할 수 없습니다. |
| 닉네임 길이 제한 위반  | Bad Request | 닉네임은 2 ~ 20자 길이여야 합니다. |
| 공백이 포함된 닉네임   | Bad Request | 닉네임은 공백을 포함될 수 없습니다. |
| 올바르지 않은 파일명   | Bad Request | 파일명 또는 확장자가 올바르지 않습니다. |
| 비어있는 파일명      | Bad Request | 파일명은 비워둘 수 없습니다. |
| 올바르지 않은 파일타입  | Bad Request | 파일 형식이 올바르지 않습니다. |
| 비어있는 파일타입     | Bad Request | 파일 미디어 타입은 비워둘 수 없습니다. |
| 올바르지 않는 파일 크기 | Bad Request | 파일 크기가 올바르지 않습니다. |
| 파일 크기 제한 초과   | Bad Reqeust | 10MB 이하의 파일만 업로드할 수 있습니다. |
| 비어있는 파일 크기    | Bad Request | 파일 크기는 비워둘 수 없습니다. |
</details>

<details>
<summary>회원 탈퇴</summary>

현재 로그인한 회원의 탈퇴를 요청합니다.
해당 계정은 소프트 딜리트 처리되며, 30일 후 자동으로 영구 삭제됩니다.
삭제 전까지는 복구가 가능합니다.

- 요청문 예시
```shell
curl -H "Authorization: Bearer AAAAPIuf0L+qfDkMABQ3IJ8heq2m...dbvsiQbPbP1/cxva23n7mQShtfK4pchdk/rc=" \
     -X DELETE "https://skypedia.com/api/v1/member"
```

- 응답 바디 예시
```json
{
  "message": "회원이 탈퇴되었습니다.",
  "result": {
    "removedAt": "2025-01-01T00:00:00",
    "willDeletedAt": "2025-01-31T00:00:00"
  }
}
```

| Field         | Description                    |
|---------------|--------------------------------|
| removedAt     | 탈퇴 요청이 승인된 날짜 및 시간입니다.         |
| willDeletedAt | 회원 데이터가 영구적으로 삭제되는 날짜 및 시간입니다. |

- 에러 메시지 목록

| Situation | Status      | Message                       |
|-----------|-------------|-------------------------------|
| 인증정보 없음   | Unauthorized | 해당 서비스를 사용하기 위해서는 로그인이 필요합니다. |
| 이미 탈퇴함    | Bad Request | 이미 탈퇴한 회원입니다.                 |
</details>

<details>
<summary>회원 프로필 조회</summary>

회원 테이블의 ID와 일치하는 회원의 프로필 데이터를 조회합니다.

- 요청문 예시
```shell
curl -H "Authorization: Bearer AAAAPIuf0L+qfDkMABQ3IJ8heq2m...dbvsiQbPbP1/cxva23n7mQShtfK4pchdk/rc=" \
     -X GET "https://skypedia.com/api/v1/admin/member/1"
```

- 응답 바디 예시
```json
{
  "message": "프로필을 성공적으로 조회했습니다.",
  "result": {
    "nickname": "username1234",
    "email": "username1234@gmail.com",
    "socialType": "GOOGLE",
    "photoUrl": "http://image12345.com/qewfunokvozcxvmapdfas.png",
    "removed": false
  }
}
```

- 에러 메시지 목록

| Situation | Status      | Message                       |
|-----------|-------------|-------------------------------|
| 인증정보 없음   | Unauthorized | 해당 서비스를 사용하기 위해서는 로그인이 필요합니다. |
| 접근권한 없음 | Forbidden | 해당 리소스에 대한 접근 권한이 없습니다. |
| 잘못된 회원 ID값 | Bad Request | 회원의 ID 값은 0이상이어야 합니다. |
</details>

<details>
<summary>회원 목록 검색</summary>

회원 데이터를 검색합니다.
검색 옵션에 따라 닉네임과 이메일을 함께 또는 각각 기준으로 검색할 수 있으며,
검색 결과는 정확도순, 최신순, 오래된순으로 정렬됩니다.
한 페이지에는 최대 20개의 결과가 포함됩니다.

- 요청문 예시
```shell
curl -H "Authorization: Bearer AAAAPIuf0L+qfDkMABQ3IJ8heq2m...dbvsiQbPbP1/cxva23n7mQShtfK4pchdk/rc=" \
     -X GET "https://skypedia.com/api/v1/admin/member?option='MEMBER_NICKNAME'&keyword='username'&sort='NEWEST'&page=0"
```

| Parameter | Description                                                          |
|-----------|----------------------------------------------------------------------|
| option    | 검색할 옵션명입니다. 기본값은 닉네임입니다. (MEMBER_ALL, MEMBER_NICKNAME, MEMBER_EMAIL) |
| keyword   | 검색 키워드입니다.                                                           |
| sort      | 검색 결과를 정렬할 조건입니다. 기본값은 최신순입니다. (RELEVANCE, OLDEST, NEWEST)           |
| page      | 검색한 목록의 페이지입니다. 각 페이지의 항목은 20개씩 보여집니다.                               |

- 응답 바디 예시
```json
{
  "message": "데이터를 성공적으로 조회했습니다.",
  "result": {
    "page": 0,
    "totalPages": 1,
    "totalCount": 2,
    "isFirst": true,
    "isLast": true,
    "data": [
      {
        "id": 1,
        "nickname": "username1",
        "email": "username1@gmail.com",
        "socialType": "GOOGLE",
        "photoUrl": "https://skypedia.com/asdfasdf.png",
        "roleTypes": ["USER", "ADMIN"],
        "createdAt": "2025-01-01T00:00:00",
        "removedAt": null
      },
      {
        "id": 3,
        "nickname": "username3",
        "email": "username3@daum.net",
        "socialType": "KAKAO",
        "photoUrl": "https://skypedia.com/zxcvzxcv.png",
        "roleTypes": ["USER"],
        "createdAt": "2025-01-02T00:00:00",
        "removedAt": null
      }
    ]
  }
}
```

- 에러 메시지 목록

| Situation | Status       | Message                       |
|-----------|--------------|-------------------------------|
| 인증정보 없음   | Unauthorized | 해당 서비스를 사용하기 위해서는 로그인이 필요합니다. |
| 접근권한 없음   | Forbidden    | 해당 리소스에 대한 접근 권한이 없습니다.       |
| 잘못된 검색 옵션 | Bad Request | 사용할 수 없는 검색 옵션입니다. |
| 잘못된 정렬 조건 | Bad Request | 사용할 수 없는 정렬 옵션입니다. |
| 잘못된 페이지 번호 | Bad Request | 페이지는 0 또는 음수값이 될 수 없습니다. |
| 검색결과 없음   | Not Found    | 검색 결과가 존재하지 않습니다.             |
</details>

<details>
<summary>강제 탈퇴</summary>

회원 테이블의 ID와 일치하는 회원의 탈퇴를 요청합니다.
해당 계정은 소프트 딜리트 처리되며, 30일 후 자동으로 영구 삭제됩니다.
삭제 전까지는 복구가 가능합니다.

- 요청문 예시
```shell
curl -H "Authorization: Bearer AAAAPIuf0L+qfDkMABQ3IJ8heq2m...dbvsiQbPbP1/cxva23n7mQShtfK4pchdk/rc=" \
     -X DELETE "https://skypedia.com/api/v1/admin/member/1"
```

- 응답 바디 예시
```json
{
  "message": "해당 회원을 강제탈퇴 처리했습니다."
}
```

- 에러 메시지 목록

| Situation | Status       | Message                       |
|-----------|--------------|-------------------------------|
| 인증정보 없음   | Unauthorized | 해당 서비스를 사용하기 위해서는 로그인이 필요합니다. |
| 접근권한 없음   | Forbidden    | 해당 리소스에 대한 접근 권한이 없습니다.       |
| 잘못된 회원 ID값 | Bad Request | 회원의 ID 값은 0이상이어야 합니다. |
| 회원이 존재하지 않음 | Not Found | 해당 회원이 존재하지 않습니다. |
| 이미 탈퇴함    | Bad Request | 이미 탈퇴한 회원입니다.                 |
</details>

<details>
<summary>복구 처리</summary>

회원 테이블의 ID와 일치하는 회원의 탈퇴를 취소합니다.
30일의 유예기간 동안만 가능하며, 일시가 지날경우 복구가 불가능합니다.

- 요청문 예시

```shell
curl -H "Authorization: Bearer AAAAPIuf0L+qfDkMABQ3IJ8heq2m...dbvsiQbPbP1/cxva23n7mQShtfK4pchdk/rc=" \
     -X PATCH "https://skypedia.com/api/v1/admin/member/1"
```

- 응답 바디 예시

```json
{
  "message": "해당 회원을 복구 처리했습니다."
}
```

- 에러 메시지 목록

| Situation  | Status       | Message                       |
|------------|--------------|-------------------------------|
| 인증정보 없음    | Unauthorized | 해당 서비스를 사용하기 위해서는 로그인이 필요합니다. |
| 접근권한 없음    | Forbidden    | 해당 리소스에 대한 접근 권한이 없습니다.       |
| 잘못된 회원 ID값 | Bad Request | 회원의 ID 값은 0이상이어야 합니다.         |
| 회원이 존재하지 않음 | Not Found | 해당 회원이 존재하지 않습니다. |
| 탈퇴하지 않은 회원 | Bad Request | 탈퇴하지 않은 회원은 복구할 수 없습니다.       |

</details>

<details>
<summary>관리자 권한 부여</summary>

일반 회원에게 관리자 권한을 부여합니다.

- 요청문 예시

```shell
curl -H "Authorization: Bearer AAAAPIuf0L+qfDkMABQ3IJ8heq2m...dbvsiQbPbP1/cxva23n7mQShtfK4pchdk/rc=" \
     -X PATCH "https://skypedia.com/api/v1/admin/member/role/1"
```

- 응답 바디 예시

```json
{
  "message": "해당 회원에 관리자 권한을 부여했습니다."
}
```

- 에러 메시지 목록

| Situation   | Status       | Message                       |
|-------------|--------------|-------------------------------|
| 인증정보 없음     | Unauthorized | 해당 서비스를 사용하기 위해서는 로그인이 필요합니다. |
| 접근권한 없음     | Forbidden    | 해당 리소스에 대한 접근 권한이 없습니다.       |
| 잘못된 회원 ID값  | Bad Request | 회원의 ID 값은 0이상이어야 합니다.         |
| 회원이 존재하지 않음 | Not Found | 해당 회원이 존재하지 않습니다.             |
| 이미 권한 부여됨   | Bad Request | 해당 회원에게 이미 부여된 역할입니다.         |

</details>

<details>
<summary>관리자 권한 제거</summary>

관리자 회원의 권한을 제거합니다.

- 요청문 예시

```shell
curl -H "Authorization: Bearer AAAAPIuf0L+qfDkMABQ3IJ8heq2m...dbvsiQbPbP1/cxva23n7mQShtfK4pchdk/rc=" \
     -X DELETE "https://skypedia.com/api/v1/admin/member/role/1"
```

- 응답 바디 예시

```json
{
  "message": "해당 회원의 관리자 권한을 제거했습니다."
}
```

- 에러 메시지 목록

| Situation   | Status       | Message                       |
|-------------|--------------|-------------------------------|
| 인증정보 없음     | Unauthorized | 해당 서비스를 사용하기 위해서는 로그인이 필요합니다. |
| 접근권한 없음     | Forbidden    | 해당 리소스에 대한 접근 권한이 없습니다.       |
| 잘못된 회원 ID값  | Bad Request  | 회원의 ID 값은 0이상이어야 합니다.         |
| 회원이 존재하지 않음 | Not Found    | 해당 회원이 존재하지 않습니다.             |
| 제거할 권한 없음   | Not Found    | 제거할 역할이 존재하지 않습니다.            |

</details>