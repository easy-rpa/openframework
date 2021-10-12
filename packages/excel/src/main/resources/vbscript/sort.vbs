On Error Resume Next
xlSortOnValues = 0
xlAscending = 1
xlDescending = 2
xlSortNormal =0
xlYes = 1
xlTopToBottom = 1
xlPinYin = 1

Set excel = CreateObject("Excel.Application")
Set workbook = excel.Workbooks.Open(Wscript.Arguments(0))
excel.Application.DisplayAlerts = False
excel.Visible = False

Set sheet = workbook.Sheets(Wscript.Arguments(1))
header = Split(Wscript.Arguments(2), ":")(0)
sheet.Range(header).AutoFilter
sheet.AutoFilter.Sort.SortFields.Clear

If Wscript.Arguments(3) = "AtoZ" Then
	sortType = xlAscending
Else
	sortType = xlDescending
End If

sheet.AutoFilter.Sort.SortFields.Add sheet.Range(Wscript.Arguments(2)), xlSortOnValues, sortType, ,xlSortNormal 

sheet.AutoFilter.Sort.Header = xlYes
sheet.AutoFilter.Sort.MatchCase = False
sheet.AutoFilter.Sort.Orientation = xlTopToBottom
sheet.AutoFilter.Sort.SortMethod = xlPinYin
sheet.AutoFilter.Sort.Apply

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

'script call example 
'cscript sort.vbs "C:\Scripts\VBS\ChartofAccounts984.xlsx" "ChartofAccounts" "B1:B459" "ZtoA"