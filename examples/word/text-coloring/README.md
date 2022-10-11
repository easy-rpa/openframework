## Coloring text

This example demonstrates using a using of Word library functionality to create text color.
In our case, we tried to make the most suitable business case. 
Which is finding a part of the text by a keyword, and coloring two words before the keyword and, accordingly, after it.

```java
    WordDocument doc = new WordDocument("source.docx");
    Picture picture = new Picture("image.png");

    //find text by regexp
    TextRange range = doc.findText(".*Java.*");
    
    //color the first word
    range.format().color(Colors.RED);

    //navigate the cursor one word to the right
    range.expandRight();
    range.expandRight();

    range.format().color(Colors.RED);
    
    doc.saveAs("output.docx");

```

See the full source of this example for more details or check following instructions to run it.

### Running

> :warning: **To be able to build and run this example it's necessary to have an access
> to some instance of EasyRPA Control Server.**

Its a fully workable process. To play around with it and run do the following:

1. Download this example using [link][down_git_link].
2. Unpack it somewhere on local file system.
3. Specify URL to the available instance of EasyRPA Control Server in the `pom.xml` of this example:
    ```xml
    <repositories>
        <repository>
            <id>easy-rpa-repository</id>
            <url>[Replace with EasyRPA Control Server URL]/nexus/repository/easyrpa/</url>
        </repository>
    </repositories>
    ```
4. If necessary, change version of `easy-rpa-engine-parent` in the same `pom.xml` to corresponding version of EasyRPA
   Control Server:
    ```xml
    <parent>
        <groupId>eu.ibagroup</groupId>
        <artifactId>easy-rpa-engine-parent</artifactId>
        <version>[Replace with version of EasyRPA Control Server]</version>
    </parent>
    ```

5. Build it using `mvn clean install` command. This command should be run within directory of this example.
6. Run `main()` method of `TextColoringModule` class.

[down_git_link]: https://downgit.github.io/#/home?url=https://github.com/easy-rpa/openframework/tree/main/examples/word/text-coloring

### Configuration

All necessary configuration files can be found in `src/main/resources` directory.

**apm_run.properties**

<table>
    <tr><th>Parameter</th><th>Value</th></tr>
      <tr><td valign="top"><code>output.files.dir</code></td><td>
        Path on local file system where modified Word file will be written.
    </td></tr>
    <tr><td valign="top"><code>source.document.file</code></td><td>
        Path to Word file that has to be read. It can be path on local file system or within resources of this 
        module.
    </td></tr>
</table>
