On Error Resume Next

xlHidden = 0
xlRowField = 1
xlColumnField = 2
xlFilterField = 3
xlDataField = 4

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

Sub setFilter(pivotTable, filterField)
    filterFieldArgs = Split(filterField, ":")
	pivotTable.PivotFields(filterFieldArgs(0)).orientation = xlFilterField
End Sub

Sub setRow(pivotTable, rowField)
    rowFieldArgs = Split(rowField, ":")
	pivotTable.PivotFields(rowFieldArgs(0)).orientation = xlRowField
End Sub

Sub setColumn(pivotTable, columnField)
    columnFieldArgs = Split(columnField, ":")
	pivotTable.PivotFields(columnFieldArgs(0)).orientation = xlColumnField
End Sub

'TODO Updating of value fields is not working. Needs to be rewrite/fixed.
Sub setValue(pivotTable, valueField)
	valueFieldArgs = Split(valueField, ":")
	pivotTable.AddDataField pivotTable.PivotFields(valueFieldArgs(0)),  valueFieldArgs(1), summarizeTypeConst(valueFieldArgs(2))
End Sub

'Sub updateValue(pivotTable, valueField)
'	valueFieldArgs = Split(valueField, ":")
'	pivotTable.PivotFields(valueFieldArgs(0)).f
'	With pivotTable.PivotFields(valueFieldArgs(0))
 '       .caption = valueFieldArgs(1)
 '      .function = summarizeTypeConst(valueFieldArgs(2))
'	End With
'End Sub

Function summarizeTypeConst(valueSummarizeType)
    Select Case valueSummarizeType
         Case "SUM"
            summarizeTypeConst = xlSum
         Case "COUNT"
             summarizeTypeConst = xlCount
         Case "AVERAGE"
             summarizeTypeConst = xlAverage
         Case "MAX"
             summarizeTypeConst = xlMax
         Case "MIN"
             summarizeTypeConst = xlMin
         Case "PRODUCT"
             summarizeTypeConst = xlProduct
         Case "COUNT_NUMBERS"
             summarizeTypeConst = xlCountNums
         Case "STDDEV"
             summarizeTypeConst = xlStDev
         Case "STDDEVP"
             summarizeTypeConst = xlStDevP
         Case "VAR"
             summarizeTypeConst = xlVar
         Case "VARP"
             summarizeTypeConst = xlVarP
    End Select
End Function

Set excel = CreateObject("Excel.Application")
Set workbook = excel.Workbooks.Open(Wscript.Arguments(0))
excel.Application.DisplayAlerts = False
excel.Visible = False

pivotTablePositionParts = Split(Wscript.Arguments(3), "!")
pivotTableSheetName = Replace(pivotTablePositionParts(0), "'", "")
Set pivotTableSheet = workbook.Sheets(pivotTableSheetName)
pivotTableSheet.Select

action = Wscript.Arguments(1)
If "CREATE" = action Then
    Set pivotTable = workbook.PivotCaches.Create(1, Wscript.Arguments(4), 5).CreatePivotTable(Wscript.Arguments(3), Wscript.Arguments(2), 5)
Else
    Set pivotTable = pivotTableSheet.PivotTables(Wscript.Arguments(2))

    If UBound(pivotTablePositionParts) > 1 Then
        pivotTable.location = Wscript.Arguments(3)
    End If

    If Not IsEmpty(Wscript.Arguments(4)) Then
        pivotTable.ChangePivotCache workbook.PivotCaches.Create(1, Wscript.Arguments(4), 5)
    End If

    pivotTable.PivotCache.Refresh
End If   

filterFields = Split(Wscript.Arguments(5), ",")
For Each filterField In filterFields
	setFilter pivotTable, filterField
Next

columnFields = Split(Wscript.Arguments(6), ",")
For Each columnField In columnFields
	setColumn pivotTable, columnField
Next

rowFields = Split(Wscript.Arguments(7), ",")
For Each rowField In rowFields
	setRow pivotTable, rowField
Next

valueFields = Split(Wscript.Arguments(8), ",")
For Each valueField In valueFields
     setValue pivotTable, valueField
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
