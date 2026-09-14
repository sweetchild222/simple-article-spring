
# Open Spring


이것은 구현한 Java Spring 프로그램으로 입니다. 아래는 사이트가 배포된 주소입니다.
누구나 회원 가입을 하고 블로그를 운영할 수 있는 서비스입니다.
마크 다운 형식으로 글을 작성할 수 있습니다. 구현된 사이트는 아래와 같습니다.

https://leafstory.click


https://Open React


JPA나 MyBartis를 사용하지 않고, 저 수준의 MySQL을 구현합니다. Spring을 구현합니다.

현재


<style>
    table {
        
        border-collapse: collapse; /* Apply to the table element */
        thead {

            tr{
                th{
                    border : 1px solid;            
                }
            }       
        }

        tbody {
            tr{
                td{
                    
                    border: 2px solid red;
                    background-color: #fff;
                    background-clip: padding-box; /* Fixes background overlap */
                }
                
                
            }
        }
    }

</style>

<details>


  <summary>Alarm</summary>
    <table style="border-collapse: collapse;">
        <caption>알람 관련 API</caption>
        <thead>
            <tr>
                <th>Method</th>
                <th>URL</th>
                <th>Query parameter</th>
                <th>Request payload</th>
                <th>Response payload</th>
                <th>Access authroity</th>
            </tr>
        </thead>
        <tbody style="white-space: pre;">
            <tr>
                <td>api/alarm</td>
                <td>{<br/>&emsp;aaa:"sdfs",<br/>&emsp;sdfsdf:"sdf",<br/>}</td>
                <td>500원</td>
                <td>Permit all</td>
                <td>Authenticated</td>
                <td>Authenticated</td>
            </tr>
            <tr>
                <td>중학생이상</td>
                <td>2,000원</td>
                <td>800원</td>
                <td>300원</td>
                <td>500원</td>
                <td>500원</td>
            </tr>
            <tr>
                <td>초등학생</td>
                <td>700원</td>
                <td>500원</td>
                <td>300원</td>
                <td>500원</td>
                <td>500원</td>
            </tr>
        </tbody>
    </table>
</details>
