#!/bin/bash

# Create bin directory if it doesn't exist
mkdir -p ../bin

# Compile all Java files with SQLite JDBC in classpath
echo "Compiling Java files..."
javac -d ../bin -cp "../lib/sqlite-jdbc-3.42.0.0.jar:." *.java database/*.java

# Copy database schema to bin directory
echo "Copying database files..."
mkdir -p ../bin/database
cp database/schema.sql ../bin/database/

# Create JAR file with dependencies
echo "Creating JAR file..."
cd ../bin
jar cvfe ToDoList.jar Main *.class database/
cd ../src

# Create run script
echo "Creating run script..."
cat > ../run.sh << EOF
#!/bin/bash
java -cp "lib/sqlite-jdbc-3.42.0.0.jar:bin/ToDoList.jar" Main
EOF
chmod +x ../run.sh

echo "Build completed successfully!"
echo "Run the application with: ./run.sh"
