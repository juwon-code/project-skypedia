### 에러 API

#### 심볼 목록
✔️(작업 완료), ⚠️(작업 예정), ❌(검토 필요), 🧑(일반 회원), 🛡️(관리자)

#### API 목록
| Situation         | Status      | Description                       | Done |
|-------------------|-------------|-----------------------------------|:----:|
| 쿼리 파라미터 검증 실패     | Bad Request | 요청 API의 쿼리 파라미터 검증이 실패했습니다.       |✔️|
| 경로 변수 검증 실패       | Bad Request | 요청 API의 경로 변수 검증이 실패했습니다.         |✔️|
| 요청 바디 검증 실패       | Bad Request | 요청 API의 바디 데이터 검증이 실패했습니다.        |✔️|
| URI 파라미터 타입 변환 실패 | Bad Request | URI 파라미터의 필드의 타입을 변환에 실패했습니다.     |✔️|
| JSON 역직렬화 실패      | Bad Request | 요청 바디의 잘못된 구조로 인해 역직렬화에 실패했습니다.   |✔️|
| 잘못된 HTTP 메소드 사용 | Method Not Allowed | 요청 URL에 대해 잘못된 HTTP 메소드를 사용했습니다.  |✔️| 
| 잘못된 헤더 사용 | Not Acceptable | 잘못된 Accept 헤더로 요청을 보냈습니다.         |✔️|

<details>
<summary>쿼리 파라미터 검증 실패</summary>

API 요청에서 쿼리 파라미터에 대해 서버에서 설정한 조건에 맞지 않는 잘못된 값이 있을 경우,
발생하는 예외를 핸들링하고 에러 메시지를 전달하는 API 입니다.

- 요청문 예시

```shell
curl -H "Authorization: Bearer AAAAPIuf0L+qfDkMABQ3IJ8heq2m...dbvsiQbPbP1/cxva23n7mQShtfK4pchdk/rc=" \
     -X GET "https://skypedia.com/api/v1/admin/member?page=-1"
```

회원 검색 API를 호출할 때, 페이지 번호를 음수값으로 호출하는 요청문입니다.

- 응답 바디 예시

```json
{
  "message": "요청 경로 또는 파라미터 값이 유효하지 않습니다.",
  "details": {
    "page": "페이지는 음수값이 될 수 없습니다."
  }
}
```
</details>

<details>
<summary>경로 변수 검증 실패</summary>

API 요청에서 경로 변수에 대해 서버에서 설정한 조건에 맞지 않는 잘못된 값이 있을 경우,
발생하는 예외를 핸들링하고 에러 메시지를 전달하는 API 입니다.

- 요청문 예시

```shell
curl -H "Authorization: Bearer AAAAPIuf0L+qfDkMABQ3IJ8heq2m...dbvsiQbPbP1/cxva23n7mQShtfK4pchdk/rc=" \
     -X GET "https://skypedia.com/api/v1/admin/member/-1"
```

회원 조회 API를 호출할 때, 회원 ID 값(경로 변수)을 음수값으로 호출하는 요청문입니다.

- 응답 바디 예시

```json
{
  "message": "요청 경로 또는 파라미터 값이 유효하지 않습니다.",
  "details": {
    "memberId": "회원의 ID 값은 0이상이어야 합니다."
  }
}
```
</details>

<details>
<summary>요청 바디 검증 실패</summary>

API 요청 바디에 대해 서버에서 설정한 조건에 맞지 않는 잘못된 값이 있을 경우,
발생하는 예외를 핸들링하고 에러 메시지를 전달하는 API입니다.

- 요청문 예시

```shell
curl -H "Authorization: Bearer AAAAPIuf0L+qfDkMABQ3IJ8heq2m...dbvsiQbPbP1/cxva23n7mQShtfK4pchdk/rc=" \
     -H "Content-Type: application/json" \
     -d '{"nickname": "1"}'
     -X PATCH "https://skypedia.com/api/v1/member"
```

내 프로필 수정 API를 호출할 때, 요청바디에 잘못된 길이의 닉네임이 들어간 요청문입니다.

- 응답 바디 예시

```json
{
  "message": "요청 바디에 잘못된 값이 감지되었습니다.",
  "details": {
    "nickname": "닉네임은 2 ~ 20자 길이여야 합니다."
  }
}
```
</details>

<details>
<summary>URI 파라미터 타입 변환 실패</summary>

API 요청에 쿼리 파라미터, 경로 변수에 포함된 필드의 값을 변환하지 못했을 경우,
발생하는 예외를 핸들링하고 에러 메시지를 전달하는 API입니다.
잘못된 필드가 여러 개 있을 경우에도 단일값을 우선적으로 감지하여 전달합니다.

- 요청문 예시

```shell
curl -H "Authorization: Bearer AAAAPIuf0L+qfDkMABQ3IJ8heq2m...dbvsiQbPbP1/cxva23n7mQShtfK4pchdk/rc=" \
     -X GET "https://skypedia.com/api/v1/admin/member?option='WRONG_OPTION'&sort='WRONG_SORT'&page=-1"
```

회원 검색 API를 호출할 때, 변환할 수 없는 SearchOption, SortType이 들어간 요청문입니다.

- 응답 바디 예시

```json
{
  "message": "요청 타입이 잘못되었습니다.",
  "details": {
    "searchOption": "'WRONG_OPTION'은 SearchOption 타입으로 변환할 수 없습니다."
  }
}
```
</details>

<details>
<summary>JSON 역직렬화 실패</summary>

API 요청 바디에 대해 JSON 문법 위배, 잘못된 필드값 등으로 인해 역직렬화를 실패했을 경우,
발생하는 예외를 핸들링하고 에러 메시지를 전달하는 API입니다.
위배사항이 여럿 있을 경우에도 우선으로 감지된 오류 하나만을 전달합니다.

- 요청문 예시

```shell
curl -H "Authorization: Bearer AAAAPIuf0L+qfDkMABQ3IJ8heq2m...dbvsiQbPbP1/cxva23n7mQShtfK4pchdk/rc=" \
     -H "Content-Type: application/json" \
     -d '{"nickname": "1246357"'
     -X PATCH "https://skypedia.com/api/v1/member"
```

내 프로필 수정 API를 호출할 때, 요청바디에 '}'가 누락되어 잘못된 문법의 JSON을 사용한 예입니다.

- 응답 바디 예시

```json
{
  "message": "요청 본문을 읽을 수 없습니다. JSON 형식 또는 필드 타입을 확인해주세요.",
  "details": {
    "none": "JSON 형식이 올바르지 않습니다."
  }
}
```
</details>

<details>
<summary>잘못된 HTTP 메소드 사용</summary>

URL 자체는 유효하나, 잘못된 HTTP 메소드를 사용하여 API를 요청했을 경우,
발생하는 예외를 핸들링하고 에러 메시지를 전달하는 API입니다.

- 요청문 예시

```shell
curl -H "Authorization: Bearer AAAAPIuf0L+qfDkMABQ3IJ8heq2m...dbvsiQbPbP1/cxva23n7mQShtfK4pchdk/rc=" \
     -X POST "https://skypedia.com/api/v1/member"
```

회원 API에 정의되지 않은 POST 메소드로 요청을 보낸 예입니다.

- 응답 바디 예시

```json
{
  "message": "지원되지 않는 HTTP 메소드입니다.",
  "details": {
    "method": "POST",
    "supported": "GET, PUT, DELETE"
  }
}
```
</details>

<details>
<summary>잘못된 헤더 사용</summary>

요청에 서버에서 지원하지 않는 헤더를 사용하여 API를 요청했을 경우,
발생하는 예외를 핸들링하고 에러 메시지를 전달하는 API입니다.

- 요청문 예시

```shell
curl -H "Authorization: Bearer AAAAPIuf0L+qfDkMABQ3IJ8heq2m...dbvsiQbPbP1/cxva23n7mQShtfK4pchdk/rc=" \
     -H "Accept: text/html" \
     -X POST "https://skypedia.com/api/v1/member"
```

서버가 제공할 수 없는 'text/html' 헤더로 요청을 보낸 예입니다.

- 응답 바디 예시

```json
{
  "message": "요청한 미디어 타입으로 응답할 수 없습니다.",
  "details": {
    "supported": "application/json"
  }
}
```
</details>