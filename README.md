# Parser & Scanner for [Tiny Language](http://jlu.myweb.cs.uwindsor.ca/214/language.htm)

Make sure you have Java SE Development Kit 8u171 or higher

##  Third party acknowledgments
*  We used a program called [GraphViz](https://www.graphviz.org/) to draw the syntax tree so the folder named [GraphVizLite](https://github.com/AbdelrahmanAbdelrazek/Parser/tree/master/GraphVizLite) must be located right next to the [parser.jar](https://github.com/AbdelrahmanAbdelrazek/Parser/blob/master/Parser.JAR) file.

* We also [this API](https://github.com/jabbalaci/graphviz-java-api) to interact with the GraphViz program.
(so the [src/GraphViz.java](https://github.com/AbdelrahmanAbdelrazek/Parser/blob/master/src/GraphViz.java) is made by them and not us).

## User Guide
* Open Parser.jar

![](https://github.com/AbdelrahmanAbdelrazek/Parser/blob/master/images/1.png)

2. Click on “Browse” and choose the file containing the program written in TINY language.

![](https://github.com/AbdelrahmanAbdelrazek/Parser/blob/master/images/2.png)

3. Click “Parse” to output the Syntax Tree.

![](https://github.com/AbdelrahmanAbdelrazek/Parser/blob/master/images/3.png)

4. Syntax Tree will be drown.

![](https://github.com/AbdelrahmanAbdelrazek/Parser/blob/master/images/4.png)

5.	You can choose another file by clicking on “Browse” and repeat from step 2.
6.	You can also click on “save Syntax Tree” which produces a PNG file named “SyntaxTree.png” located next to “Parser.jar” file. The file will contain the image of the Syntax Tree produced by the program.

![](https://github.com/AbdelrahmanAbdelrazek/Parser/blob/master/images/5.png)
![](https://github.com/AbdelrahmanAbdelrazek/Parser/blob/master/images/6.png)

    

## Notes
*	The Syntax Trees produced follow the TINY language rules except the repeat statement.
* The Repeat Statement node should be like this.

![](https://github.com/AbdelrahmanAbdelrazek/Parser/blob/master/images/note.jpg)


  Instead the program puts the “test child” of the repeat node on the left and the “body child” on the right.

