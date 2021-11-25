On Error Resume Next

xlSortOnValues = 0
xlAscending = 1
xlDescending = 2
xlSortNormal = 0
xlYes = 1
xlTopToBottom = 1
xlPinYin = 1

Set excel = CreateObject("Excel.Application")
Set workbook = excel.Workbooks.Open(WScript.Arguments(0))
excel.Application.DisplayAlerts = False
excel.Visible = False

rangeParts = Split(WScript.Arguments(1), "!")
sheetName = Replace(rangeParts(0), "'", "")
Set sheet = workbook.Sheets(sheetName)

header = Split(rangeParts(1), ":")(0)
sheet.Range(header).AutoFilter
sheet.AutoFilter.Sort.SortFields.Clear

If WScript.Arguments(2) = "ASC" Then
	sortType = xlAscending
Else
	sortType = xlDescending
End If

sheet.AutoFilter.Sort.SortFields.Add sheet.Range(rangeParts(1)), xlSortOnValues, sortType, ,xlSortNormal
With sheet.AutoFilter.Sort
    .Header = xlYes
    .MatchCase = False
    .Orientation = xlTopToBottom
    .SortMethod = xlPinYin
    .Apply
End With

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