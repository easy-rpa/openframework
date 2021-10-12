On Error Resume Next

Set excel = CreateObject("Excel.Application")
Set workbook = excel.Workbooks.Open(Wscript.Arguments(0))
excel.Application.DisplayAlerts = False
excel.Visible = False

targetFile = Replace(Wscript.Arguments(1), "/", "\") 
targetFileFormat = Wscript.Arguments(2)

If Wscript.Arguments.Count > 3 Then
	sourceSheet = Wscript.Arguments(3)
Else
	sourceSheet = ""
End If

if sourceSheet <> "" Then
	Set sheet = workbook.Sheets(sourceSheet)
	sheet.SaveAs targetFile, targetFileFormat, 0, 0, 0, 0, 0, 0, 0, 1
Else
	workbook.SaveAs targetFile, targetFileFormat, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1
End If

If Err.Number <> 0 Then
	workbook.Close False
	excel.Application.Quit
	WScript.Echo "Error #" & Err.Number & " " & Err.Description
	WScript.Quit Err.Number
End If

workbook.Close False
excel.Application.Quit
WScript.Echo "SUCCESS"
WScript.Quit