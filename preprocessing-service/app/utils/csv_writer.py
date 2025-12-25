import pandas as pd


def write_csv(df: pd.DataFrame, path: str):
    df.to_csv(path, index=False)
