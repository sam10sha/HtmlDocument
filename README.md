# HtmlDocument
HTML Parser

Instantiate the HTMLStringFactory with the source code of any webpage, given that the page contains HTML.

LOADING OBJECTS

Load objects with 2 methods:

By ID:  loadElementById("example");
        This will find the element in the HTML document with the specified ID, in this case, "example."

By type and index:  loadElementByTypeAndIndex("table", 2);
                    This will load the 2nd "table" element in the entire document.

SCOPE

After loading the elements, the scope of the HTMLStringFactory will change to only the selected element, instead of the entire HTML page.

To reset the scope of the HTMLStringFactory to the entire document, use:
loadNewElementById() and loadNewElementByTypeAndIndex()

HTML ELEMENT

To receive the entire HTML as a single HTMLElement, use the commands:

loadNewElementByTypeAndIndex("body", 0);
getElement();

This will provide all the HTML in a single HTMLElement.

The HTMLElement can be used to objectify all objects in the HTML page. With HTMLElement, the title, content, attributes, and child elements can be retrieved. Simply use the methods:
getTitle():     Retrieves the title of the element, such as "table" or "select".
getContent()    Retrieves the elements between the opening and closing tags of the HTML element.
getAttributeValue(String attributeName) Retrieves the attribute value with the specified attribute name.
                                        For example: getAttributeValue("bgcolor") would return the background color
getChildElementList()   Retrieves the list of child elements of the loaded HTML element
