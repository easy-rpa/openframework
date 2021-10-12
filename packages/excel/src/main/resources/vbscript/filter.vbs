On Error Resume Next
xlFilterValues = 7

Set excel = CreateObject("Excel.Application")
Set workbook = excel.Workbooks.Open(Wscript.Arguments(0))
excel.Application.DisplayAlerts = False
excel.Visible = False

'Prepare a regular expression object
Set regExpFilter = New RegExp
regExpFilter.IgnoreCase = False
regExpFilter.Global = False
regExpFilter.Pattern = Replace(Wscript.Arguments(2), "*", ".")

Dim filteredValues()
filterIndex = 0

For Each c in workbook.Sheets(Wscript.Arguments(1)).Range(Wscript.Arguments(4))
	Set mathes  = regExpFilter.Execute(c.Text)
	 If mathes.Count <> 0 Then
	 	ReDim Preserve filteredValues(filterIndex)
		filteredValues(filterIndex) = c.Text
		filterIndex = filterIndex + 1
	 End If
Next

workbook.Sheets(Wscript.Arguments(1)).Range(Wscript.Arguments(3)).AutoFilter CInt(Wscript.Arguments(5)), filteredValues, xlFilterValues

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


'cscript %RPA_SCRIPT%\filter.vbs "C:\Scripts\VBS\ChartofAccounts984.xlsx" "ChartofAccounts" "19***" "A1:H460" "B2:B460" "2"
