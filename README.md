
# Open Spring


이것은 구현한 Java Spring 프로그램으로 입니다. 아래는 사이트가 배포된 주소입니다.
누구나 회원 가입을 하고 블로그를 운영할 수 있는 서비스입니다.
마크 다운 형식으로 글을 작성할 수 있습니다. 구현된 사이트는 아래와 같습니다.

https://leafstory.click


https://Open React


JPA나 MyBartis를 사용하지 않고, 저 수준의 MySQL을 구현합니다. Spring을 구현합니다.

현재



<details>
  <summary>Alarm</summary>
    <table style="border-collapse: collapse;">
        <caption>댓글, 답글에 대한 알람</caption>
        <thead style="background-color:gray; color:lightGray;">
            <tr>                
                <th style="border: 1px solid; width:400px;">Address</th>
                <th style="border: 1px solid; width:400px;">Request payload</th>
                <th style="border: 1px solid; width:400px;">Response payload</th>            
            </tr>
        </thead>
        <tbody style="white-space: pre; color: white;">
            <tr>                
                <td style="border: 1px solid;"><i>Get</i><br/>api/user/:userId/alarm</td>
                <td style="border: 1px solid"></td>
                <td style="border: 1px solid">[<br/>&emsp;{<br/>&emsp;&emsp;"article_id": 561,<br/>&emsp;&emsp;"blog_id": 37,<br/>&emsp;&emsp;"to_user_id": 218,<br/>&emsp;&emsp;"checked": 1,<br/>&emsp;&emsp;"comment": "Very good",<br/>&emsp;&emsp;"id": 134,<br/>&emsp;&emsp;"type": "COMMENT",<br/>&emsp;&emsp;"create_at": 1787822522000,<br/>&emsp;&emsp;"comment_id": 724,<br/>&emsp;&emsp;"from_user_id": 217<br/>&emsp;}<br/>]</td>                
            </tr>
            <tr>                
                <td style="border: 1px solid"><i>Post</i><br/>api/alarm</td>
                <td style="border: 1px solid">{<br/>&emsp;to_user_id: 3,<br/>&emsp;from_user_id: 10,<br/><div title="or 'REPLY', 'MENTION'">&emsp;<ins>type: 'COMMENT',</ins>&emsp;</div>&emsp;comment_id: 15<br/>}</td>
                <td style="border: 1px solid">{<br/><div title="Created alarm id">&emsp;<ins>id: 30</ins></div>}</td>                
            </tr>
            <tr>                
                <td style="border: 1px solid">Patch<br/>api/alarm/:alarmId</td>
                <td style="border: 1px solid">{<br/>&emsp;checked: 1,&emsp;//or 0<br/>}</td>
                <td style="border: 1px solid"></td>            
            </tr>
            <tr>                
                <td style="border: 1px solid">Delete<br/>api/alarm/:alarmId</td>
                <td style="border: 1px solid"></td>
                <td style="border: 1px solid"></td>
            </tr>
        </tbody>
    </table>
</details>



<details>
  <summary>Article</summary>
    <table style="border-collapse: collapse;">
        <caption>게시글</caption>
        <thead style="background-color:gray; color:lightGray;">
            <tr>                
                <th style="border: 1px solid">Address</th>
                <th style="border: 1px solid">Request payload</th>
                <th style="border: 1px solid">Response payload</th>
                <th style="border: 1px solid">Access constraint</th>
            </tr>
        </thead>
        <tbody style="white-space: pre; color: white;">
            <tr>                
                <td style="border: 1px solid">Get<br/>api/article?<br/>offset=10&<br/>limit=5&<br/>order=0&<br/><div style="text-decoration: underline;" title="or like_count, comment_count">order_type=post_at&</div>//or like_count, comment_count<br/>id=3,5,6&<br/>blog_id=15,20&<br/>keyword='good'&emsp;<br/>//to search in content or title</td>
                <td style="border: 1px solid"></td>
                <td style="border: 1px solid">[<br/>&emsp;{<br/>&emsp;&emsp;"id": 5,<br/>&emsp;&emsp;"title": "this is title",<br/>&emsp;&emsp;"thumbnail": "https://image.webp",<br/>&emsp;&emsp;"blog_id": 39,<br/>&emsp;&emsp;"bookmark_count": 1,<br/>&emsp;&emsp;"posted": 1,&emsp;//0 means in progress<br/>&emsp;&emsp;"head": "this is ..",&emsp;//article's content head<br/>&emsp;&emsp;"category_id": 210,<br/>&emsp;&emsp;"user_id": 34,<br/>&emsp;&emsp;"showed": 120,<br/>&emsp;&emsp;"update_at": 1789363211000,<br/>&emsp;&emsp;"source_id": 36,&emsp;//original article id, when modifing posted<br/>&emsp;&emsp;"post_at": 1788495370000,<br/>&emsp;&emsp;"create_at": 1788495330000,<br/>&emsp;&emsp;"comment_count": 1,<br/>&emsp;&emsp;"dislike_count": 3,<br/>&emsp;&emsp;"like_count": 9<br/>&emsp;}<br/>]</td>
                <td style="border: 1px solid">Permit all</td>
            </tr>
            <tr>                
                <td style="border: 1px solid">Post<br/>api/article</td>
                <td style="border: 1px solid">{<br/>&emsp;title: 'this is title',<br/>&emsp;content: 'this is content',<br/>&emsp;head: 'this is head',&emsp;//article'scontent head<br/>&emsp;posted: 1,&emsp;//0 means in progress<br/>&emsp;thumbnail: 'https://image.webp',<br/>&emsp;category_id: 10,<br/>&emsp;souruce_id: 55,<br/>}</td>
                <td style="border: 1px solid">{<br/>&emsp;id: 343&emsp;//created article id<br/>}</td>
                <td style="border: 1px solid">the category's blog_id and authenticated blog_id must be equal</td>
            </tr>
            <tr>                
                <td style="border: 1px solid">Put<br/>api/article/:articleId</td>
                <td style="border: 1px solid">{<br/>&emsp;title: 'this is title',<br/>&emsp;content: 'this is content',<br/>&emsp;head: 'this is head',&emsp;//article'scontent head<br/>&emsp;posted: 1,&emsp;//0 means in progress<br/>&emsp;thumbnail:&emsp;'https://image.webp',<br/>&emsp;category_id: 10,<br/>}</td>
                <td style="border: 1px solid"></td>
                <td style="border: 1px solid">the category's blog_id and authenticated blog_id must be equal</td>
            </tr>
            <tr>                
                <td style="border: 1px solid">Delete<br/>api/article/:articleId</td>
                <td style="border: 1px solid"></td>
                <td style="border: 1px solid"></td>
                <td style="border: 1px solid">the article's blog_id and authenticated blog_id must be equal</td>
            </tr>
            <tr>                
                <td style="border: 1px solid">Get<br/>api/blog/:blogId/article?<br/>offset=10&<br/>limit=5&<br/>order=0&<br/>//0 is ascending, 1 is descending<br/>posted=1&&emsp;//0 means in progres<br/>category_id=5&<br/>min_article_id=10&&emsp;<br/>max_article_id=30&emsp;</td>
                <td style="border: 1px solid"></td>
                <td style="border: 1px solid">[<br/>&emsp;{<br/>&emsp;&emsp;"id": 15,<br/>&emsp;&emsp;"title": "this is title",<br/>&emsp;&emsp;"thumbnail": "https://image.webp",<br/>&emsp;&emsp;"blog_id": 39,<br/>&emsp;&emsp;"bookmark_count": 1,<br/>&emsp;&emsp;"posted": 1,&emsp;//0 means in progress<br/>&emsp;&emsp;"head": "this is ..",&emsp;//article's content head<br/>&emsp;&emsp;"category_id": 210,<br/>&emsp;&emsp;"user_id": 34,<br/>&emsp;&emsp;"showed": 120,<br/>&emsp;&emsp;"update_at": 1789363211000,<br/>&emsp;&emsp;"source_id": 36,	//original article id, when modifing posted<br/>&emsp;&emsp;"post_at": 1788495370000,<br/>&emsp;&emsp;"create_at": 1788495330000,<br/>&emsp;&emsp;"comment_count": 1,<br/>&emsp;&emsp;"dislike_count": 3,<br/>&emsp;&emsp;"like_count": 9<br/>&emsp;}<br/>]</td>
                <td style="border: 1px solid">Permit all</td>
            </tr>
            <tr>                
                <td style="border: 1px solid">Post<br/>api/article/:articleId/showed</td>
                <td style="border: 1px solid"></td>
                <td style="border: 1px solid"></td>
                <td style="border: 1px solid">Permit all</td>
            </tr>
        </tbody>
    </table>
</details>






<details>
  <summary>Article great</summary>
    <table style="border-collapse: collapse;">
        <caption>게시글 좋아요</caption>
        <thead style="background-color:gray; color:lightGray;">
            <tr>                
                <th style="border: 1px solid">Address</th>
                <th style="border: 1px solid">Request payload</th>
                <th style="border: 1px solid">Response payload</th>
                <th style="border: 1px solid">Access constraint</th>
            </tr>
        </thead>
        <tbody style="white-space: pre; color: white;">
            <tr>                
                <td style="border: 1px solid"><em>Get</em><br/>api/article/great?<br/>user_id=217&&emsp;<br/>article_id=817</td>                
                <td style="border: 1px solid"></td>
                <td style="border: 1px solid">[<br/>&emsp;{<br/>&emsp;&emsp;"id":588,<br/>&emsp;&emsp;"article_id":817,<br/>&emsp;&emsp;"user_id":217,<br/>&emsp;&emsp;"great":1<br/>&emsp;}<br/>]</td>
                <td style="border: 1px solid">Permit all</td>
            </tr>            
            <tr>                
                <td style="border: 1px solid">Post<br/>api/article/great</td>
                <td style="border: 1px solid">{<br/>&emsp;user_id: 30,<br/>&emsp;article_id: 70,<br/>&emsp;great: 0&emsp;//0 means disgreat, 1 means  great<br/>}</td>
                <td style="border: 1px solid">{<br/>&emsp;id: 343&emsp;//created great id<br/>}</td>
                <td style="border: 1px solid">the user_id and authenticated user_id must be equal</td>
            </tr>
            <tr>                
                <td style="border: 1px solid">Patch<br/>api/article/:greatId</td>
                <td style="border: 1px solid">{<br/>&emsp;great: 0&emsp;//0 means disgreat, 1 means  great<br/>}</td>
                <td style="border: 1px solid"></td>
                <td style="border: 1px solid">the user_id of the great and authenticated user_id must be equal</td>
            </tr>
            <tr>                
                <td style="border: 1px solid">Delete<br/>api/article/great/:greatId</td>
                <td style="border: 1px solid"></td>
                <td style="border: 1px solid"></td>
                <td style="border: 1px solid">the user_id of the great and authenticated user_id must be equal</td>
            </tr>
        </tbody>
    </table>
</details>
