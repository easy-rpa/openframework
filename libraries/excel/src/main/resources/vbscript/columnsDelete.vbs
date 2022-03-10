On Error Resume Next

xlShiftToLeft=-4159

Set excel = CreateObject("Excel.Application")
Set workbook = excel.Workbooks.Open(WScript.Arguments(0))
excel.Application.DisplayAlerts = False
excel.Visible = False

positionParts = Split(WScript.Arguments(1), "!")
sheetName = Replace(positionParts(0), "'", "")
Set sheet = workbook.Sheets(sheetName)

sheet.Columns(positionParts(1)).Delete xlShiftToLeft

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
