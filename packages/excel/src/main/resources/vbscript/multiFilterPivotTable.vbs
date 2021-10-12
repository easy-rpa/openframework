On Error Resume Next
Sub applyMode(sheet, pivotTableName, field, mode)
	If "ALL" = mode Then
		sheet.PivotTables(pivotTableName).PivotFields(field).CurrentPage = "(All)"
	Else
		For Each pivotItem In sheet.PivotTables(pivotTableName).PivotFields(field).PivotItems 
	 		pivotItem.Visible = False
		Next
	End If   
End Sub

Set excel = CreateObject("Excel.Application")
Set workbook = excel.Workbooks.Open(Wscript.Arguments(0))
excel.Application.DisplayAlerts = False
excel.Visible = False

Set sheet = workbook.Sheets(Wscript.Arguments(1))
pivotTableName  = Wscript.Arguments(2)
pivotTableField = Wscript.Arguments(3)
pivotTableFilterMode = Wscript.Arguments(4)

applyMode sheet, pivotTableName, pivotTableField, pivotTableFilterMode

selectItems = Split(Wscript.Arguments(5), ",")
For Each selectItem In selectItems
	sheet.PivotTables(pivotTableName).PivotFields(pivotTableField).PivotItems(selectItem).Visible = True
Next

unselectItems = Split(Wscript.Arguments(6), ",")
For Each unselectItem In unselectItems
	sheet.PivotTables(pivotTableName).PivotFields(pivotTableField).PivotItems(unselectItem).Visible = False
Next

sheet.PivotTables(pivotTableName).PivotFields(pivotTableField).EnableMultiplePageItems = True

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

'cscript %RPA_SCRIPT%\multiFilterPivotTable.vbs "C:\Users\andreid\Documents\Excel docks\CA postings 2018-08.xlsx" "Total" "PivotTable2" "GL account" "ALL" "" "83410"