On Error Resume Next
xlRowField = 1
xlColumnField = 2
xlFilterField = 3

xlSum = -4157
xlCount = -4112
xlAverage = -4106
xlMax = -4136
xlMin = -4139
xlProduct = -4149
xlCountNums = -4113
xlVarP = -4165
xlVar = -4164
xlStDevP = -4156
xlStDev = -4155

Sub createPivoteTable(workbook, from, target, pivotTableName)
	workbook.PivotCaches.Create(1, from, 6).CreatePivotTable target, pivotTableName,  , 6	  
End Sub

Sub addFilter(sheet, pivotTableName, filterField)
	sheet.PivotTables(pivotTableName).PivotFields(filterField).orientation = xlFilterField
End Sub

Sub addRow(sheet, pivotTableName, rowField)
	sheet.PivotTables(pivotTableName).PivotFields(rowField).orientation = xlRowField
End Sub

Sub addColumn(sheet, pivotTableName, columnField)
	sheet.PivotTables(pivotTableName).PivotFields(columnField).orientation = xlColumnField
End Sub

Sub addValue(sheet, pivotTableName, valueField)
	valueFieldArgs = Split(valueField, ":")
	
	
	If UBound(valueFieldArgs) = 1 Then
   		Select Case valueFieldArgs(1)
			Case "SUM"
				sheet.PivotTables(pivotTableName).AddDataField sheet.PivotTables(pivotTableName).PivotFields(valueFieldArgs(0)),  "Sum of " & valueFieldArgs(0), xlSum
	        Case "COUNT"
				sheet.PivotTables(pivotTableName).AddDataField sheet.PivotTables(pivotTableName).PivotFields(valueFieldArgs(0)),  "Count of " & valueFieldArgs(0), xlCount
			Case "AVERAGE"
				sheet.PivotTables(pivotTableName).AddDataField sheet.PivotTables(pivotTableName).PivotFields(valueFieldArgs(0)),  "Average of " & valueFieldArgs(0), xlAverage
			Case "MAX"
				sheet.PivotTables(pivotTableName).AddDataField sheet.PivotTables(pivotTableName).PivotFields(valueFieldArgs(0)),  "Max of " & valueFieldArgs(0), xlMax
			Case "MIN"
				sheet.PivotTables(pivotTableName).AddDataField sheet.PivotTables(pivotTableName).PivotFields(valueFieldArgs(0)),  "Min of " & valueFieldArgs(0), xlMin
			Case "PRODUCT"
				sheet.PivotTables(pivotTableName).AddDataField sheet.PivotTables(pivotTableName).PivotFields(valueFieldArgs(0)),  "Product of " & valueFieldArgs(0), xlProduct
			Case "COUNT_NUMBERS"
				sheet.PivotTables(pivotTableName).AddDataField sheet.PivotTables(pivotTableName).PivotFields(valueFieldArgs(0)),  "Count of " & valueFieldArgs(0), xlCountNums
			Case "STDDEV"
				sheet.PivotTables(pivotTableName).AddDataField sheet.PivotTables(pivotTableName).PivotFields(valueFieldArgs(0)),  "StdDev of " & valueFieldArgs(0), xlStDev
			Case "STDDEVP"
				sheet.PivotTables(pivotTableName).AddDataField sheet.PivotTables(pivotTableName).PivotFields(valueFieldArgs(0)),  "StdDevp of " & valueFieldArgs(0), xlStDevP
			Case "VAR"
				sheet.PivotTables(pivotTableName).AddDataField sheet.PivotTables(pivotTableName).PivotFields(valueFieldArgs(0)),  "Var of " & valueFieldArgs(0), xlVar
			Case "VARP"
				sheet.PivotTables(pivotTableName).AddDataField sheet.PivotTables(pivotTableName).PivotFields(valueFieldArgs(0)),  "Varp  of " & valueFieldArgs(0), xlVarP
		 	Case Else
				sheet.PivotTables(pivotTableName).AddDataField sheet.PivotTables(pivotTableName).PivotFields(valueFieldArgs(0))
	     End Select 
	 Else
	 	sheet.PivotTables(pivotTableName).AddDataField sheet.PivotTables(pivotTableName).PivotFields(valueFieldArgs(0))
	End If
End Sub

Set excel = CreateObject("Excel.Application")
Set workbook = excel.Workbooks.Open(Wscript.Arguments(0))
excel.Application.DisplayAlerts = False
excel.Visible = False

pivotSheetName = Split(Wscript.Arguments(2), "!")(0)
Set pivoteTableSheet = workbook.Sheets(pivotSheetName)
pivoteTableSheet.Select

pivotTableName = Wscript.Arguments(3)
createPivoteTable workbook, Wscript.Arguments(1), Wscript.Arguments(2), pivotTableName

filterFields = Split(Wscript.Arguments(4), ",")
For Each filterField In filterFields
	addFilter pivoteTableSheet, pivotTableName, filterField
Next

columnFields = Split(Wscript.Arguments(5), ",")
For Each columnField In columnFields
	addColumn pivoteTableSheet, pivotTableName, columnField
Next

rowFields = Split(Wscript.Arguments(6), ",")
For Each rowField In rowFields
	addRow pivoteTableSheet, pivotTableName, rowField
Next

valueFields = Split(Wscript.Arguments(7), ",")
For Each valueField In valueFields
	addValue pivoteTableSheet, pivotTableName, valueField
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

'cscript %RPA_SCRIPT%\createPivoteTable.vbs "C:\Scripts\VBS\MtM vs external valuation 2018-03-31.xlsx" "MTM!R7C1:R100C43" "Total!R3C1" "PivotTable1" "AH,Status" "" "" "Asset,Liabiity:COUNT"