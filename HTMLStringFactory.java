package big_boiz.antplanner;

import java.util.ArrayList;

/**
 * HTMLStringFactory helper class handles HTML pages
 * Provides an interface to retrieve HTML elements in the page
 *
 * Created by Saumil Shah
 */
public class HTMLStringFactory {

    /**
     * HTMLElement class makes up a single element in the HTML page provided
     * Provides an interface to retrieve attributes of the element
     *
     * Created by Saumil Shah
     */
	public class HTMLElement {
		private String Title, Content;          //Title: the type of element
                                                //Content: the contents in between the element tags
		private ArrayList<String> AttributeList;    //The list of attributes of the element
                                                    //Located in the opening tag of the element

		private ArrayList<HTMLElement> ChildElementList;	//The list of child elements

        /**
         * Constructs an instance of a single HTMLElement
         *
         * @param item    The String element retrieved from the HTML document
         */
		public HTMLElement(String item) {
			AttributeList = new ArrayList<>();
			ChildElementList = new ArrayList<>();
			findTitle(item);
			findContent(item, Title);
			findChildElements();
			findAttributes(item);
		}

        /**
         * Cleans the content of the HTML element i.e. replaces all the special character
         * entities with the actual special characters
         */
		private void cleanContent() {
			String[] codes = {"&nbsp;", "&amp;"};
			String[] substitutions = {"", "&"};

			int i;
			
			if(Content != null) {
				for(i = 0; i < codes.length; i++)
					Content = Content.replaceAll(codes[i], substitutions[i]);
			}
		}

        /**
         * Finds the title of the HTML element, which is the name of the type of element
         * @param item    The entire HTML element
         */
		private void findTitle(String item) {
			int i, length;

			length = item.length();
			i = 0;

			while(i < length && item.charAt(i) != '>' && item.charAt(i) != ' ')
				i++;
			Title = item.substring(1, i);
		}

        /**
         * Finds the content of the element, which is the information in between
         * the two item title tags
         * @param item    		The entire HTML element
         * @param elementTitle	The title of the current element
         */
		private void findContent(String item, String elementTitle) {
			int i, j, record,
					length, startLength, endLength;
			String startSearch, endSearch;

			i = 1;
			record = 1;
			length = item.length();
			
			while(i < length && item.charAt(i) != '>')
				i++;

            if(item.charAt(i-1) == '/')
                Content = null;
            else {
            	startSearch = "<" + elementTitle;
                endSearch = "</" + elementTitle + ">";
                startLength = startSearch.length();
                endLength = endSearch.length();
                j = ++i;

				while (record != 0) {
					if (item.substring(i, i + endLength).equals(endSearch))
						record--;
					else if (item.substring(i, i + startLength).equals(startSearch))
						record++;
					i++;
				}

                Content = item.substring(j, i-1);
                cleanContent();
            }
		}

		/**
		 * Finds the child elements of the element
		 * Requires that Content be found first
		 */
		private void findChildElements() {
			int i, j,								//i:		index for parsing item
													//j:		placeholder for i
					count,							//count:	counts the number of childElement
													//			tags that appear inside an element
					contentLength;					//contentLength: length of the entire element's
													//				 content
			boolean step1, step2, step3, step4;		//step1:	find tag opener: '<'
													//step2:	find end of tag name
													//step3:	find end of tag: '>'
													//step4:	find end of element
			String startWord, endWord;				//startWord: the opening tag of a child element
													//endWord: the closing tag of the child element
			HTMLElement htmlElement;		//an HTMLElement to add to the list of child elements

			i = 0;
			j = -1;
			count = -1;
			step1 = true;
			step2 = false;
			step3 = false;
			step4 = false;
			startWord = null;
			endWord = null;

			if(Content != null) {
				contentLength = Content.length();
				while(i < contentLength) {
					if(step1 && Content.charAt(i) == '<') {	//finding tag opener
						step1 = false;
						step2 = true;
						j = i;
					}
					if(step2)								//finding end of tag name
						if(Content.charAt(i) == ' ' || Content.charAt(i) == '>' || Content.charAt(i) == '/') {
							startWord = "<" + Content.substring(j+1, i);
							endWord = "</" + Content.substring(j+1, i) + ">";
							count = 1;
							step2 = false;
							step3 = true;
						}
					if(step3 && Content.charAt(i) == '>') {	//finding end of tag
						if(Content.charAt(i-1) == '/') {
							htmlElement = new HTMLElement(Content.substring(j, i+1));
							ChildElementList.add(htmlElement);
							count = -1;
							step1 = true;
						}
						else
							step4 = true;
						step3 = false;
					}
					if(step4) {								//finding end of element
						if (i + startWord.length() <= Content.length() &&
								Content.substring(i, i + startWord.length()).equals(startWord))
							count++;
						else if (i + endWord.length() <= Content.length() &&
								Content.substring(i, i + endWord.length()).equals(endWord))
							count--;
						if (count == 0) {
							htmlElement = new HTMLElement(Content.substring(j, i + endWord.length()));
							ChildElementList.add(htmlElement);
							count = -1;
							step4 = false;
							step1 = true;
						}
					}

					i++;
				}
			}
		}

        /**
         * Finds the attributes associated with the HTML element
         * These attributes are the items inside the header element title tag,
         * which generally include the style, format, layout, and other
         * general aspects of the HTML element
         * @param item    The entire HTML element
         */
		private void findAttributes(String item) {
			int i, j,
					length;
            boolean record;
			String attribute;

			i = 0;
			j = -1;
            record = false;
			length = item.length();
			while(i < length && item.charAt(i) != ' ' && item.charAt(i) != '>')
				i++;

			while(i < length && item.charAt(i) != '>') {
				if(item.charAt(i) != ' ' && j < 0)
					j = i;
				else if(j >= 0 && item.charAt(i) == '\"' && !record)
					record = true;
				else if(j >= 0 && item.charAt(i) == '\"' && record) {
					attribute = item.substring(j, i+1);
					AttributeList.add(attribute);
					j = -1;
					record = false;
				}

				i++;
			}
		}

        /**
         * Retrieves the title of the HTML element
         * @return  The title of the element
         */
		public String getTitle() {
			return Title;
		}

        /**
         * Retrieves the content inside of the HTML element
         * This content can include text or other HTML elements
         * @return  The content in the HTML element
         */
		public String getContent() {
			return Content;
		}

		/**
		 * Retrieves the child elements inside of the HTML element
		 * @return  The child elements of the HTML element
		 */
		public ArrayList<HTMLElement> getChildElementList() {
			return ChildElementList;
		}

        /**
         * Finds the attribute specified by attributeName and returns the value associated
         * with this attribute
         * @param attributeName    The attribute to find
         * @return  The value associated with the attribute
         */
		public String getAttributeValue(String attributeName) {
			int i, length;
			String current;

			length = attributeName.length();
			for(i = 0; i < AttributeList.size(); i++) {
				current = AttributeList.get(i);
				if(length < current.length() &&
                        current.substring(0, length).equals(attributeName))
                    return current.substring(length + 2, current.length() - 1);
			}
			return null;
		}
	}

	private String HTML, query;     //HTML: the HTML document which intialized the HTMLStringFactory
                                    //      class
                                    //query: the current focus in the entire HTML document; this
                                    //       focus can be the entire HTML document, or a particular
                                    //       element inside the HTML document

	private int elementStartIndex, elementEndIndex; //elementStartIndexx: The starting index of the
                                                    //                    current element in the
                                                    //                    entire HTMl
                                                    //elementEndIndex:    The ending index of the
                                                    //                    current element in the
                                                    //                    entire HTMl
	private String elementType; //The title of the current element

    /**
     * Constructs an instance of the HTMLStringFactory
     * @param HTML    The HTML code of the document
     */
	public HTMLStringFactory(String HTML) {
		this.HTML = HTML;
		query = HTML;
	}

    /**
     * Loads the ending index of the HTML element
     * @param startIndex    The starting index of the element in question
     * @param type          The title of the current element
     */
	private void loadEndIndex(int startIndex, String type) {
		int i, HTMLLength,
				length, startElementLength, numStartElement;
		String search, secondarySearch, startElement;

		search = "/" + type + ">";
		secondarySearch = "/>";
		startElement = "<" + type;
		length = search.length();
		startElementLength = startElement.length();
		numStartElement = 1;
		HTMLLength = query.length();
		i = startIndex + startElementLength;

		while(query.charAt(i) != '>')
            i++;
        if(query.charAt(i-1) == '/') {
            search = secondarySearch;;
            length = search.length();
        }

		while(numStartElement > 0) {
			if(query.substring(i, i + startElementLength).equals(startElement))
				numStartElement++;

			else if(i+length <= HTMLLength && query.substring(i, i + length).equals(search))
				numStartElement--;

			i++;
		}

        elementEndIndex = i + length - 1;
	}

	/**
     * Loads an element by parsing the current query, searching for the specified ID
     *
     * @param id    The ID attribute of the element
     * @throws HTMLParseException   Thrown when an element of the specified ID is not found
     *                              in the current query
     */
	public void loadElementById(String id)  throws HTMLParseException {
		int i,
				HTMLLength, length;
		String search;

		i = 0;
		search = "name=\""+id+"\"";
		length = search.length();
		HTMLLength = query.length();

		while(i+length <= HTMLLength && !query.substring(i, i+length).equals(search))
			i++;
		if(i+length <= HTMLLength) {
			while(query.charAt(i) != '<')
				i--;
			elementStartIndex = i;

			while(query.charAt(i) != ' ')
				i++;
			elementType = query.substring(elementStartIndex+1, i);

			loadEndIndex(elementStartIndex, elementType);

            query = HTML.substring(elementStartIndex, elementEndIndex);
		}
		else
			throw new HTMLParseException("loadElementById(): No match found for: " + id);
			//System.out.println("loadElementById(): No match found for: " + id);
	}

	/**
     * Resets the query to the entire HTML and then searches for the element with the specified ID
     * @param id    The
     * @throws HTMLParseException   Thrown when an element of the specified ID is not found in the
     *                              entire document
     */
    public void loadNewElementById(String id) throws HTMLParseException {
        query = HTML;
        elementType = null;
        elementStartIndex = 0;
        elementEndIndex = 0;
        loadElementById(id);
    }

    /**
     * Loads an element by parsing the current query, searching for an element of the specified
     * type at the specified index
     * Index of 0 indicates the first element of the type in the document
     * @param type	The type of element for which to search the document
     * @param index	The number of the element to search for
     * @throws HTMLParseException	Thrown if no element is found at the specified index
     */
	public void loadElementByTypeAndIndex(String type, int index) throws HTMLParseException {
		int i, record,
				queryLength, numElements,
				length, endLength;
		String search, endSearch, errMessage;
		HTMLElement element;
		
		if(elementType != null && elementType.equals(type)) {
			element = new HTMLElement(query);
			query = element.getContent();
		}

		i = 0;
		record = 0;
		search = "<" + type;
		endSearch = "</" + type + ">";
		length = search.length();
		endLength = endSearch.length();
		queryLength = query.length();
		numElements = -1;

		while(i+length <= queryLength && i+endLength <= queryLength
				&& numElements < index) {
			if(query.substring(i, i+length).equals(search)) {
				if(record == 0)
					numElements++;
				record++;
			}
			else if(query.substring(i, i+endLength).equals(endSearch))
				record--;
			
			/* if(query.substring(i, i+length).equals(search))
				numElements++; */
			i++;
		}
		//if(i+length <= HTMLLength) {
		if(numElements == index) {
			elementStartIndex = --i;

			while(query.charAt(i) != ' ' && query.charAt(i) != '>')
				i++;
			elementType = query.substring(elementStartIndex+1, i);

			loadEndIndex(elementStartIndex, elementType);

            query = query.substring(elementStartIndex, elementEndIndex);
		}
		else {
			errMessage = "loadElementByTypeAndIndex(): Index out of bounds";
			errMessage += "\nElement of type \"" + type + "\" at index \"" + index + "\" not found";
			errMessage += "\nMaximum number of elements of current type: " + numElements;
			throw new HTMLParseException(errMessage);
			/* System.out.println("loadElementByTypeAndIndex(): Index out of bounds");
			System.out.println("Element of type \"" + type + "\" at index \"" + index + "\" not found");
			System.out.println("Maximum number of elements of current type: " + numElements);

			return false; */
		}

	}

	/**
	 * Resets the query to the entire HTML and then searches for the element with the specified type
	 * at the specified index
	 * @param type	The type of element for which to search the document
	 * @param index	The number of the element to search for
	 * @throws HTMLParseException	Thrown if no element is found at the specified index
	 */
    public void loadNewElementByTypeAndIndex(String type, int index) throws HTMLParseException {
        query = HTML;
        elementType = null;
        elementStartIndex = 0;
        elementEndIndex = 0;
        loadElementByTypeAndIndex(type, index);
    }

    /**
     * Parses through the current query and retrieves all the elements displayed in the drop-down list
     * @return	2 lists, one containing all of the options of the drop-down list, and the second
     * 			of all of the values of all of the options of the list
     * @throws HTMLParseException	Thrown if no element has been loaded, or if the loaded element
     * 								is not of the correct type
     */
    public ArrayList[] getDropDownItemsAndValues() throws HTMLParseException {
        int i, j,
                elementLength, endWordLength, startKeyWordLength, endKeyWordLength;
        String element, startWord, endWord, startKeyWord, endKeyWord;
        HTMLElement item;
        ArrayList<HTMLElement> DropDownItems;
        ArrayList<String> DropDownStrings;

        //element = HTML.substring(elementStartIndex, elementEndIndex);
        element = query;
        startWord = "select";
        endWord = "</select>";
        startKeyWord = "<option";
        endKeyWord = "</option>";
        endWordLength = endWord.length();
        startKeyWordLength = startKeyWord.length();
        endKeyWordLength = endKeyWord.length();
        DropDownItems = new ArrayList<>();
        DropDownStrings = new ArrayList<>();
        ArrayList[] itemValuePairs = new ArrayList[2];

        if(elementType == null)
        	throw new HTMLParseException("getDropDownItems(): No element loaded");

        if(!elementType.equals(startWord))
        	throw new HTMLParseException("getDropDownItems(): Element is not of type \"select\"");

        i = 0;
        j = -1;
        elementLength = element.length();
        while(i+endWordLength < elementLength && !element.substring(i, i+endWordLength).equals(endWord)) {
            if(element.substring(i, i+startKeyWordLength).equals(startKeyWord))
                j = i;
            else if(element.substring(i, i+endKeyWordLength).equals(endKeyWord)) {
                item = new HTMLElement(element.substring(j, i+endKeyWordLength));
                DropDownItems.add(item);
                j = -1;
            }

            i++;
        }
        for(HTMLElement in : DropDownItems)
            DropDownStrings.add(in.getContent());

        itemValuePairs[0] = DropDownStrings;
        itemValuePairs[1] = DropDownItems;

        return itemValuePairs;
    }

    /**
     * Parses through the current query and retrieves all the elements displayed in the table
     * @return	A list of all of the rows in the table in, each row containing the individual
	 * 			HTMLElement for each table cell
     * @throws HTMLParseException	Thrown if no element has been loaded, or if the loaded element
     * 								is not of the correct type
     */
	public ArrayList<ArrayList> getTableElements() throws HTMLParseException {
		int i, j,
				elementLength, endWordLength,
				startKeyWordLength, startCellWordLength, endCellWordLength;
		String element, endWord,
				startKeyWord, startCellWord_1, startCellWord_2,
				endCellWord_1, endCellWord_2;
		HTMLElement item;
		ArrayList<ArrayList> tableRows;
		ArrayList<HTMLElement> singleTableRow;
		ArrayList<String> singleTableRowString;

		element = query;
		endWord = "</table>";
		startKeyWord = "<tr";
		startCellWord_1 = "<td";
		endCellWord_1 = "</td>";
		startCellWord_2 = "<th";
		endCellWord_2 = "</th>";
		elementLength = element.length();
		endWordLength = endWord.length();
		startKeyWordLength = startKeyWord.length();
		startCellWordLength = startCellWord_1.length();
		endCellWordLength = endCellWord_1.length();
		tableRows = new ArrayList<>();
		singleTableRow = null;
		singleTableRowString = null;

		i = 0;
		j = -1;

		if(elementType == null)
			throw new HTMLParseException("getTableElements(): No element loaded");

		if(!elementType.equals("table"))
			throw new HTMLParseException("getTableElements(): Loaded element not of type \"table\"");

		while(i+endWordLength < elementLength) {
			if(element.substring(i, i+startKeyWordLength).equals(startKeyWord)) {
				singleTableRow = new ArrayList<>();
				singleTableRowString = new ArrayList<>();
				tableRows.add(singleTableRow);
			}

			else if(element.substring(i, i+startCellWordLength).equals(startCellWord_1) ||
					element.substring(i, i+startCellWordLength).equals(startCellWord_2))
				j = i;
			else if(element.substring(i, i+endCellWordLength).equals(endCellWord_1) ||
					element.substring(i, i+endCellWordLength).equals(endCellWord_2)) {
				item = new HTMLElement(element.substring(j, i+endCellWordLength));
				singleTableRow.add(item);
				singleTableRowString.add(item.getContent());
			}

			i++;
		}
		return tableRows;
	}

	/**
	 * Converts the string query into an HTMLElement object
	 * @return	The converted HTMLElement object
	 */
    public HTMLElement getElement() {
        return new HTMLElement(query);
    }
}