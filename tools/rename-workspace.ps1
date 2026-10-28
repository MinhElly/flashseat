[CmdletBinding(SupportsShouldProcess)]
param(
    [string]$SourceDirectory = (Split-Path -Parent $PSScriptRoot)
)

$ErrorActionPreference = 'Stop'
$targetName = 'marketing-ai-agent-team'
$sourceItem = Get-Item -LiteralPath $SourceDirectory -ErrorAction Stop
if (-not $sourceItem.PSIsContainer) {
    throw 'SourceDirectory must be a directory.'
}
if (($sourceItem.Attributes -band [System.IO.FileAttributes]::ReparsePoint) -ne 0) {
    throw 'Refusing to move a symbolic link or directory junction.'
}

$sourceRoot = $sourceItem.FullName.TrimEnd('\')
$parentRoot = Split-Path -Parent $sourceRoot
if (-not $parentRoot) {
    throw 'Refusing to rename a filesystem root.'
}
$destinationRoot = [System.IO.Path]::GetFullPath((Join-Path $parentRoot $targetName)).TrimEnd('\')

if ($sourceRoot -eq $destinationRoot) {
    Write-Output ('Workspace already has the expected name: ' + $destinationRoot)
    return
}
if (-not (Test-Path -LiteralPath (Join-Path $sourceRoot '.git') -PathType Container)) {
    throw 'Expected a standalone Git checkout at SourceDirectory.'
}
if (-not (Test-Path -LiteralPath (Join-Path $sourceRoot 'marketing-ai-agent-team-backend\pom.xml') -PathType Leaf)) {
    throw 'Expected the renamed backend module; refusing to move an unrelated directory.'
}
if (Test-Path -LiteralPath $destinationRoot) {
    throw ('Destination already exists; no overwrite or merge will be attempted: ' + $destinationRoot)
}

if ($PSCmdlet.ShouldProcess($sourceRoot, ('Rename workspace directory to ' + $destinationRoot))) {
    # Release the current PowerShell process's directory reference before moving.
    Set-Location -LiteralPath $parentRoot
    [System.Environment]::CurrentDirectory = $parentRoot
    try {
        Move-Item -LiteralPath $sourceRoot -Destination $destinationRoot -ErrorAction Stop
    } catch {
        throw ('Could not rename the workspace. Close Codex/IDE/terminals using the directory, then retry. No processes were killed. Original error: ' + $_.Exception.Message)
    }
    if (-not (Test-Path -LiteralPath (Join-Path $destinationRoot '.git') -PathType Container)) {
        throw 'Move completed but Git metadata was not found at the destination; inspect before further work.'
    }
    Write-Output ('Workspace renamed: ' + $destinationRoot)
    Write-Output 'Reopen the project from this path in Codex and the IDE. No stage, commit, push, or database operation was performed.'
}
