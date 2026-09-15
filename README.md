


<details>
  <summary>Alarm API</summary>
    <table style="border-collapse: collapse;">        
        <thead style="background-color:gray; color:lightGray;">
            <tr>                
                <th style="border: 1px solid;">Request</th>
                <th style="border: 1px solid;">Response</th>
                <th style="border: 1px solid">Access constraint</th>
            </tr>
        </thead>
        <tbody style="white-space: pre; color: white;">
            <tr>
                <td style="border: 1px solid;"><i>Get</i> api/user/:userId/alarm</td>
                <td style="border: 1px solid">[<br>&emsp;{<br>&emsp;&emsp;article_id: 561,<br>&emsp;&emsp;blog_id: 37,<br>&emsp;&emsp;to_user_id: 218,<br>&emsp;&emsp;checked: 1,<br>&emsp;&emsp;comment: 'Very good',<br>&emsp;&emsp;id: 134,<br>&emsp;&emsp;<span title="or 'REPLY', 'MENTION'"><ins>type: 'COMMENT',</ins></span><br>&emsp;&emsp;create_at: 1787822522000,<br>&emsp;&emsp;comment_id: 724,<br>&emsp;&emsp;from_user_id: 217<br>&emsp;}<br>]</td>
                <td style="border: 1px solid">the userId<br>and<br>authenticated user_id<br>must be equal</td>
            </tr>
            <tr>
                <td style="border: 1px solid"><i>Post</i> api/alarm<br><br>{<br>&emsp;to_user_id: 3,<br>&emsp;from_user_id: 10,<br>&emsp;<span title="or 'REPLY', 'MENTION'"><ins>type: 'COMMENT',</ins></span><br>&emsp;comment_id: 15<br>}</td>
                <td style="border: 1px solid">{<br><div title="Created alarm id">&emsp;<ins>id: 30</ins></div>}</td>
                <td style="border: 1px solid">the from_user_id<br>and<br>authenticated user_id<br>must be equal</td>
            </tr>
            <tr>
                <td style="border: 1px solid"><i>Patch</i> api/alarm/:alarmId<br><br>{<br>&emsp;checked: 1<br>}</td>
                <td style="border: 1px solid"></td>
                <td style="border: 1px solid">the to_user_id of the alarm<br>and<br>authenticated user_id <br>must be equal</td>
            </tr>
            <tr>
                <td style="border: 1px solid"><i>Delete</i> api/alarm/:alarmId</td>
                <td style="border: 1px solid"></td>
                <td style="border: 1px solid">the to_user_id of the alarm<br>and<br>authenticated user_id <br>must be equal</td>
            </tr>
        </tbody>
    </table>
</details>



<details>
  <summary>Article API</summary>
    <table style="border-collapse: collapse;">
        <thead style="background-color:gray; color:lightGray;">
            <tr>
                <th style="border: 1px solid;">Request</th>
                <th style="border: 1px solid;">Response</th>
                <th style="border: 1px solid;">Access constraint</th>
            </tr>
        </thead>
        <tbody style="color: white;">
            <tr>
                <td style="border: 1px solid;"><i>Get</i> api/article?<br>offset=10&limit=5&<br><span title='or like_count, comment_count'><ins>order_type=post_at</ins></span>&order=0&<br>id=3,5,6&blog_id=15,20&<br><span title="to search in content or title"><ins>keyword='good'</ins></span></td>
                <td style="border: 1px solid">[<br>&emsp;{<br>&emsp;&emsp;id: 5,<br>&emsp;&emsp;title: 'this is title',<br>&emsp;&emsp;thumbnail: 'https://a.webp',<br>&emsp;&emsp;blog_id: 39,<br>&emsp;&emsp;bookmark_count: 1,<br>&emsp;&emsp;<span title='0 means in progress'><ins>posted: 1,</ins></span><br>&emsp;&emsp;<spa title="the article's content head"><ins>head: 'this is ..',</ins></span><br>&emsp;&emsp;category_id: 210,<br>&emsp;&emsp;user_id: 34,<br>&emsp;&emsp;showed: 120,<br>&emsp;&emsp;update_at: 1789363211000,<br>&emsp;&emsp;<span title="original article id, when modifing posted"><ins>source_id: 36,</ins></span><br>&emsp;&emsp;post_at: 1788495370000,<br>&emsp;&emsp;create_at: 1788495330000,<br>&emsp;&emsp;comment_count: 1,<br>&emsp;&emsp;dislike_count: 3,<br>&emsp;&emsp;like_count: 9<br>&emsp;}<br>]</td>
                <td style="border: 1px solid">Permit all</td>
            </tr>
            <tr>
                <td style="border: 1px solid"><i>Post</i> api/article<br><br>{<br>&emsp;title: 'this is title',<br>&emsp;content: 'this is content',<br>&emsp;<span title="the article's content head"><ins>head: 'this is head',</ins><span><br>&emsp;<span title="0 means in progress"><ins>posted: 1,</ins></span><br>&emsp;thumbnail: 'https://b.webp',<br>&emsp;category_id: 10<br>&emsp;souruce_id: 55<br>}</td>
                <td style="border: 1px solid">{<br>&emsp;<span title="created article id"><ins>id: 343</ins></span><br>}</td>
                <td style="border: 1px solid">the category's blog_id<br>and<br>authenticated blog_id<br>must be equal</td>
            </tr>
            <tr>
                <td style="border: 1px solid"><i>Put</i> api/article/:articleId</td>
                <td style="border: 1px solid">{<br>&emsp;title: 'this is title',<br>&emsp;content: 'this is content',<br>&emsp;head: 'this is head',<br>&emsp;posted: 1,<br>&emsp;thumbnail: 'https://a.webp',<br>&emsp;category_id: 10,<br>}</td>
                <td style="border: 1px solid">the category's blog_id<br>and<br>authenticated blog_id<br>must be equal</td>
            </tr>
            <tr>                
                <td style="border: 1px solid"><i>Delete</i> api/article/:articleId</td>                
                <td style="border: 1px solid"></td>
                <td style="border: 1px solid">the article's blog_id<br>and<br>authenticated blog_id<br>must be equal</td>
            </tr>
            <tr>                
                <td style="border: 1px solid"><i>Get</i> api/blog/:blogId/article?<br>offset=10&limit=5&<br><span title="0 is ascending, 1 is descending by created time"><ins>order=0&</ins></span>category_id=5&<br>min_article_id=10&<br>max_article_id=30&<br><span title="0 means in progres"><ins>posted=1</span></ins></td>                
                <td style="border: 1px solid">[<br>&emsp;{<br>&emsp;&emsp;id: 15,<br>&emsp;&emsp;title: 'this is title',<br>&emsp;&emsp;thumbnail: 'https://c.webp',<br>&emsp;&emsp;blog_id: 39,<br>&emsp;&emsp;bookmark_count: 1,<br>&emsp;&emsp;posted: 1,<br>&emsp;&emsp;head: 'this is ..',<br>&emsp;&emsp;category_id: 210,<br>&emsp;&emsp;user_id: 34,<br>&emsp;&emsp;showed: 120,<br>&emsp;&emsp;update_at: 1789363211000,<br>&emsp;&emsp;source_id: 36,<br>&emsp;&emsp;post_at: 1788495370000,<br>&emsp;&emsp;create_at: 1788495330000,<br>&emsp;&emsp;comment_count: 1,<br>&emsp;&emsp;dislike_count: 3,<br>&emsp;&emsp;like_count: 9<br>&emsp;}<br>]</td>
                <td style="border: 1px solid">Permit all</td>
            </tr>
            <tr>                
                <td style="border: 1px solid"><i>Post</i> api/article/:articleId/showed</td>
                <td style="border: 1px solid"></td>                
                <td style="border: 1px solid">Permit all</td>
            </tr>
        </tbody>
    </table>
</details>


<details>
  <summary>Article Great API</summary>
    <table style="border-collapse: collapse;">        
        <thead style="background-color:gray; color:lightGray;">
            <tr>
                <th style="border: 1px solid">Request</th>
                <th style="border: 1px solid">Response</th>
                <th style="border: 1px solid">Access constraint</th>
            </tr>
        </thead>
        <tbody style="white-space: pre; color: white;">
            <tr>
                <td style="border: 1px solid"><i>Get</i> api/article/great?<br>user_id=217&<br>article_id=817</td>
                <td style="border: 1px solid">[<br>&emsp;{<br>&emsp;&emsp;id:588,<br>&emsp;&emsp;article_id:817,<br>&emsp;&emsp;user_id:217,<br>&emsp;&emsp;great:1<br>&emsp;}<br>]</td>
                <td style="border: 1px solid">Permit all</td>
            </tr>
            <tr>
                <td style="border: 1px solid"><i>Post</i> api/article/great<br><br>{<br>&emsp;user_id: 30,<br>&emsp;article_id: 70,<br>&emsp;<span title="0 means disgreat, 1 means  great"><ins>great: 0</ins></span><br>}</td>
                <td style="border: 1px solid">{<br>&emsp;<span title="created great id"><ins>id: 343</ins></span><br>}</td>
                <td style="border: 1px solid">the user_id<br>and<br>authenticated user_id<br>must be equal</td>
            </tr>
            <tr>
                <td style="border: 1px solid"><i>Patch</i> api/article/:greatId</td>
                <td style="border: 1px solid">{<br>&emsp;great: 0<br>}</td>
                <td style="border: 1px solid">the user_id of the great<br>and<br>authenticated user_id<br>must be equal</td>
            </tr>
            <tr>
                <td style="border: 1px solid"><i>Delete</i> api/article/great/:greatId</td>
                <td style="border: 1px solid"></td>
                <td style="border: 1px solid">the user_id of the great<br>and<br>authenticated user_id<br>must be equal</td>
            </tr>
        </tbody>
    </table>
</details>
