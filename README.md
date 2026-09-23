# NSB Procurement Order Tracking System

A Java console application for managing procurement activities at the fictional company Nexus Sdn Bhd (NSB). It brings purchase requisitions, purchase orders, suppliers, inventory items, and reorder notifications into a single text-based workflow.

## Project scope

This repository focuses on the Purchase Manager module of the wider NSB procurement workflow. Purchase Manager and Administrator accounts can access the module, while other account roles are denied access.

## Features

- Login using demonstration accounts
- Purchase Manager and Administrator access to the Purchase Manager module
- Reorder-level notifications
- Item and supplier listing and filtering
- Purchase requisition viewing, filtering, creation, approval, rejection, and deletion
- Purchase order creation from approved requisitions
- Purchase order and order-item viewing and filtering
- Deletion of eligible purchase orders with related requisition-status restoration
- Text-file persistence for users, items, suppliers, requisitions, and orders

## How it works

1. A Purchase Manager or Administrator signs in to the application.
2. The system highlights inventory items that have reached their reorder level.
3. The user reviews item, supplier, and purchase requisition information.
4. Purchase requisitions can be created, approved, rejected, or deleted.
5. Approved requisitions can be selected to generate purchase orders.
6. Purchase orders and their associated items can then be viewed, filtered, or managed.

## Technologies

- Java 22-compatible source
- Java standard library
- NetBeans-generated Ant project files
- Structured text-file storage

No third-party libraries, database server, cloud account, or external service is required.

## Project structure

```text
src/oodjassignment/       Java source code
nbproject/                Shared NetBeans project configuration
build.xml                 NetBeans/Ant build entry point
users.txt                 Demonstration users and roles
item.txt                  Item and stock records
supplier.txt              Supplier records
itemSupplier.txt          Item-to-supplier prices
purchaseRequisitions.txt  Purchase requisition records
purchaseOrders.txt        Purchase order records
purchaseOrderItems.txt    Items associated with purchase orders
orderRequisitions.txt     Requisitions associated with purchase orders
```

The application expects the text files to be available in its working directory. Run it from the project root.

## Compile and run

### NetBeans (recommended)

This repository includes the original NetBeans Java project configuration.

1. Open Apache NetBeans with JDK 22 or later configured.
2. Select **File > Open Project** and choose the project directory.
3. Select **Run > Run Project**, or press **F6**.

NetBeans will compile the source and start `oodjassignment.Main` automatically.

### Visual Studio Code

The project can also be run in Visual Studio Code with the **Extension Pack for Java** installed. Open the project directory, open `src/oodjassignment/Main.java`, and select **Run Java** above the `main` method. Ensure the project directory is used as the working directory so the application can find its text data files.

### PowerShell

To compile and run without an IDE, open PowerShell in the project directory and use:

```powershell
New-Item -ItemType Directory -Force out | Out-Null
$sourceFiles = Get-ChildItem .\src\oodjassignment\*.java | ForEach-Object FullName
javac --release 22 -encoding UTF-8 -d out $sourceFiles
java -cp out oodjassignment.Main
```

Use a `purchaseManager` or `admin` demonstration account from `users.txt`. These credentials are included only for running the sample application and should not be reused elsewhere.

## Verification

- Source compilation: verified using JDK 23 with Java 22 compatibility
- Application startup and normal exit: verified
- Automated tests: not currently included
- Interactive login and data-changing business workflows: not independently runtime-verified

The application is designed for interactive terminal input. Redirecting an entire scripted input sequence may not work reliably because the code creates multiple `Scanner` instances over standard input.

## Current scope

- The repository implements the Purchase Manager workflow rather than separate modules for every organizational role.
- Persistence uses comma-separated text files rather than a database.
- Demonstration credentials are stored as plaintext and are not intended for production authentication.
- Automated tests are not currently included.
- Generated NetBeans build output and machine-specific configuration are excluded from version control.
