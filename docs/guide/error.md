### 에러 API

| Situation   | Status      | Description                 |
|-------------|-------------|-----------------------------|
| 잘못된 쿼리 파라미터 | Bad Request | 요청 API의 쿼리 파라미터 검증이 실패했습니다. |
| 잘못된 요청 바디   | Bad Request | 요청 API의 바디 검증이 실패했습니다.      |
| 타입 변환 실패    | Bad Request | 필드의 타입을 변환하던 중 에러가 발생했습니다.  |

<details>
<summary>잘못된 쿼리 파라미터</summary>

API 요청에서 쿼리 파라미터에 조건에 맞지 않는 잘못된 값이 있을 경우,
발생하는 예외를 핸들링하고 에러 메시지를 전달하는 API입니다.

- 요청문 예시

```shell
curl -H "Authorization: Bearer AAAAPIuf0L+qfDkMABQ3IJ8heq2m...dbvsiQbPbP1/cxva23n7mQShtfK4pchdk/rc=" \
     -X GET "https://skypedia.com/api/v1/admin/member?page=-1"
```

회원 검색 API를 호출할 때, 페이지 번호를 음수값으로 호출하는 요청문입니다.

- 응답 바디 예시

```json
{
  "message": "요청 쿼리에 잘못된 값이 감지되었습니다.",
  "details": {
    "page": "페이지는 음수값이 될 수 없습니다."
  }
}
```
</details>

<details>
<summary>잘못된 요청 바디</summary>

API 요청 바디에 조건에 맞지 않는 잘못된 값이 있을 경우,
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
<summary>타입 변환 에러</summary>

API 요청에 쿼리 파라미터, 바디에 포함된 필드의 값을 변환하지 못했을 경우,
발생하는 예외를 핸들링하고 에러 메시지를 전달하는 API입니다.
변환에 실패한 필드가 여러개 있을 경우에도 하나만을 우선적으로 전달합니다.

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