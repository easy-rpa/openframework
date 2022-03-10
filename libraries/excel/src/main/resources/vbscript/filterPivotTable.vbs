On Error Resume Next
Sub filterPivotTable(sheet, pivotTableName, field, value)
	sheet.PivotTables(pivotTableName).PivotFields(field).CurrentPage = value    
End Sub

Set excel = CreateObject("Excel.Application")
Set workbook = excel.Workbooks.Open(Wscript.Arguments(0))
excel.Application.DisplayAlerts = False
excel.Visible = False

Set pivoteTableSheet = workbook.Sheets(Wscript.Arguments(1))
pivotTableName  = Wscript.Arguments(2)

filters = Split(Wscript.Arguments(3), ",")
For Each fieldAndValue In filters
	field = Split(fieldAndValue, ":")(0)
	value =   Split(fieldAndValue, ":")(1)
	WScript.Echo  field & ":" & value
	filterPivotTable pivoteTableSheet, pivotTableName, field, value
Next

If Err.Number <> 0 Then
	workbook.Close False
	excel.Application.Quit
	WScript.Echo "Error #" & Err.Number & " " & Err.Description
	WScript.Quit Err.Number
End If

workbook.Save
workbook.Close True
excel.Application.Quit
WScript.Echo "SUCCESS"
WScript.Quit

'cscript %RPA_SCRIPT%\filterPivotTable.vbs "C:\Scripts\VBS\MtM vs external valuation 2018-03-31.xlsx" "Total" "PivotTable1" "AH:Actual,Status:BO validated"