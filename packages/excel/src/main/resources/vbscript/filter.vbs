' Argument 0: Excel file path to proceed
' Argument 1: range of cells that represent table on some specific sheet
' Argument 2: 1-based table column index
' Argument 3: value pattern that define subset of values that need to be displayed
' Argument 4: range of cells where corresponding values should be looked up using value pattern above

On Error Resume Next

xlFilterValues = 7

Set excel = CreateObject("Excel.Application")
Set workbook = excel.Workbooks.Open(WScript.Arguments(0))
excel.Application.DisplayAlerts = False
excel.Visible = False

rangeParts = Split(WScript.Arguments(1), "!")
sheetName = Replace(rangeParts(0), "'", "")
Set sheet = workbook.Sheets(sheetName)

'Prepare a regular expression object
Set regExpFilter = New RegExp
regExpFilter.IgnoreCase = False
regExpFilter.Global = False
regExpFilter.Pattern = WScript.Arguments(3)

Dim filteredValues()
filterIndex = 0

For Each cell in sheet.Range(WScript.Arguments(4))
	 If regExpFilter.Test(cell.Text) Then
	 	ReDim Preserve filteredValues(filterIndex)
		filteredValues(filterIndex) = cell.Text
		filterIndex = filterIndex + 1
	 End If
Next

If filterIndex > 0 Then
    sheet.Range(rangeParts(1)).AutoFilter CInt(WScript.Arguments(2)), filteredValues, xlFilterValues
Else
    sheet.Range(rangeParts(1)).AutoFilter
End If

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