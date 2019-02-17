build using maven profile setup in settings.xml.

`mvn clean install -P dev`


```$xml

 <profile>
        <id>dev</id>
            <repositories>
                <repository>
                    <id>central</id>
                    <name>central</name>
                    <url>http://repo1.maven.org/maven2</url>
                </repository>
                <repository>
                    <id>localrepository</id>
                    <url>file://${basedir}/repo</url>
                </repository>
            </repositories>
   </profile>
```
        
        