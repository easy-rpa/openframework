# EasyRPA Open Framework Examples

 [Email](#email)  
 [Excel](#excel)  
 [CSV](#csv)  
 [Google Drive](#google-drive)  
 [Google Sheets](#google-sheets)  
 [Database](#database)  
 
## Email

- [Sending of simple email message](email/simple-message-sending)

- [Sending of message with attachments](email/message-sending-with-attachments)

- [Creating of message based on FreeMarker template](email/template-based-message-creating)

- [Reading messages from inbox](email/inbox-messages-listing)

- [Searching of messages based on specified condition](email/messages-searching)

- [Waiting of message based on specified condition](email/message-waiting)

- [Reading of attached files](email/attachments-reading)

- [Mailbox messages manipulating](email/messages-manipulating) 
    * Forward message
    * Reply on message
    * Mark messages as Read/Unread    
    * Delete message   
    
- [Getting a list of existing mailbox folders](email/folders-listing)

- [Mailbox folders manipulating](email/folders-manipulating)
    * Create new folder
    * Rename folder
    * Delete folder
    
- [TODO] Work with Outlook (email/work-with-outlook)

- [TODO] Work with GMail (email/work-with-gmail)

- [TODO] Work with Exchange Server (email/work-with-exchange)

- [TODO] Work with Lotus Notes (email/work-with-lotus)

## Excel

 - [Creating of new Excel file](excel/excel-file-creating)
 
 - [Reading data from given sheet](excel/sheet-data-reading)
    * Read as range of data
    * Read as list of records    

 - [Editing of existing Excel file](excel/excel-file-editing) 
     * Edit cells
     * Edit records   
 
 - [Working with large Excel files](excel/working-with-large-files)
     * Read records   
     * Edit records
 
 - [Copying of sheet from one Excel file to another](excel/sheets-copying)    
 
 - [Sheets manipulating](excel/sheets-manipulating)
    * List existing sheets
    * Activate sheet
    * Rename sheet
    * Move sheet
    * Clone sheet
    * Delete sheet 
    
 - [Working with sheet rows](excel/working-with-rows)
    * Lookup and edit rows
    * Insert new rows
    * Delete rows
  
 - [Working with sheet columns](excel/working-with-columns)
    * Read column cells
    * Add/insert new columns
    * Move columns
    * Delete columns
    * Sort table columns 
    * Filter table columns 
    
 - [Working with formulas](excel/working-with-formulas)
    * Edit cell's formulas
    * Evaluating of cell's formulas
    * Evaluating of cell's formulas with links to external Excel files
    
 - [Working with merged cells](excel/working-with-merged-cells)
    * Read value from merged cells
    * Merge/unmerge cells 
    
 - [Working with pivot tables](excel/working-with-pivot-tables)
    * Create pivot table
    * Read pivot table 
    * Update pivot table
        
 - [Changing cells style (fonts, colors etc.)](excel/cell-style-changing)
 
 - [Inserting image to sheet](excel/image-inserting)
 
 - [Exporting Excel file to PDF](excel/export-to-pdf)
 
 - [Running of macros](excel/macros-running)
 
 - [Running of custom VB script](excel/custom-vbs-running)
 
## CSV

 - Reading data from CSV file (csv/csv-file-reading)
 
 - Writing data to CSV file (csv/csv-file-writing)
    
## Google Drive

 - [Getting a list of all files in the selected directory](google-drive/getting-files)
 - [Files manipulations](google-drive/files-manipulations)
   * Create file
   * Delete file
   * Upload file
   * Rename file
 - [Moving file from one directory to another](google-drive/file-moving)
 - [Folders manipulations](google-drive/folders-manipulations)
    * Create folder
    * Rename folder
    * Delete folder
 - [Getting information about selected file/directory](google-drive/file-dir-info)
 - [Creating Google Sheet](google-drive/google-sheet-creating)

## Google Sheets

 - Downloading a spreadsheet as an XLSX or PDF file (sheets/spreadsheet-downloading)
 - Sheet manipulations (sheets/sheet-manipulation)
    * Create sheet
    * Delete sheet
    * Rename sheet
    * Clone sheet
    * Activate sheet
    * Get list of all sheets
 - Working with sheet rows (sheets/working-with-rows)
     * Lookup and edit rows
     * Add/insert new rows
     * Delete rows
 - Working with sheet columns (sheets/working-with-columns)
   * Read column cells
   * Add/insert new columns
   * Move columns
   * Delete columns
   * Filter columns
 - Working with cells (sheets/working-with-cells)
    * Read cell formula
    * Read cell value
    * Change cell formula
    * Recalculation cell formula
 - Working with data range (sheets/working-with-data-range)
    * Reading a range of data
    * Adding data range on sheet
    * Removing data range from sheet
 
## Database
  
  **Implemented actions:**
  - [Connect to DB](database/mysql-query)
  - [Disconnect from DB](database/mysql-query)
  - [Execute an arbitrary SQL query](database/mysql-query)
  - Call stored procedure
  - Transactions support (Create / Commit / Rollback)
    
 **Supported databases:**
  - MySQL
  - PostgreSQL
  - Oracle
  - DB2
  - MS SQL Server