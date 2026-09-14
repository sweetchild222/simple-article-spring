<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Collapsible Div</title>
    <style>
        /* Style for the button/header */
        .collapsible-btn {
            background-color: #007BFF;
            color: white;
            cursor: pointer;
            padding: 15px;
            width: 100%;
            border: none;
            text-align: left;
            outline: none;
            font-size: 16px;
            font-weight: bold;
            border-radius: 4px;
        }

        /* Active class to change color on click */
        .active, .collapsible-btn:hover {
            background-color: #0056b3;
        }

        /* Style for the expandable content container */
        .content {
            padding: 0 15px;
            max-height: 0;
            overflow: hidden;
            transition: max-height 0.2s ease-out;
            background-color: #f1f1f1;
            border-radius: 0 0 4px 4px;
        }

        .content p {
            margin: 15px 0;
        }
    </style>
</head>
<body>

    <!-- Clickable Header -->
    <button class="collapsible-btn" onclick="toggleContent(this)">Click to Expand / Collapse</button>
    
    <!-- Hidden Content -->
    <div class="content">
        <p>This is the content inside the div. It will spread out when you click the button above, and collapse when you click it again!</p>
    </div>

    <script>
        function toggleContent(button) {
            // Toggle the 'active' class on the button
            button.classList.toggle("active");
            
            // Get the next element sibling (the .content div)
            var content = button.nextElementSibling;
            
            // If open, close it. If closed, open it.
            if (content.style.maxHeight) {
                content.style.maxHeight = null;
            } else {
                // scrollHeight calculates the exact height of the hidden text
                content.style.maxHeight = content.scrollHeight + "px";
            }
        }
    </script>

</body>
</html>


